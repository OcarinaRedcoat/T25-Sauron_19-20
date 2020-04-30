package pt.tecnico.sauron.silo;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.exceptions.BadEntryException;
import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.grpc.SiloGrpc;
import pt.tecnico.sauron.silo.grpc.SiloGrpc.*;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;


import java.util.*;

public class ReplicaManager {


    private List<Camera> camLog = new ArrayList<>();
    private List<Observation> obsLog = new ArrayList<>();


    private boolean connected = false;

    private int replicaId; // id of the replica
    private String path;    // general path

    private int replicaNro;

    private ZKNaming zkNaming;


    private Map<Integer, SiloBlockingStub> stubMap = new HashMap();
    private Map<Integer, ManagedChannel> channels = new HashMap();

    private List<Integer> replicaTS;

    private List<List<Integer>> tableTS;

    private SiloServerOps Ops;

    public ReplicaManager(String path, String zooHost, String zooPort, int repNro, String id){
        this.path = path;
        this.replicaNro = repNro; // total number of replicas
        this.replicaId = Integer.parseInt(id);      // replica id
        this.Ops = new SiloServerOps(this.replicaNro);

        zkNaming = new ZKNaming(zooHost, zooPort);

        tableTS = new ArrayList<>(Collections.nCopies(replicaNro - 1, new ArrayList<>(Collections.nCopies(replicaNro - 1, 0))));
        replicaTS = new ArrayList<>(Collections.nCopies(repNro, 0)); // initialize the timestamp vector with the number of replicas

    }

    public void connectReplica(){
        try {

            for (int i = 0 ; i < zkNaming.listRecords(path).size(); i++){

                if (i == replicaId - 1){ System.out.println("entrou e saiu"); continue; } // Nao conectar a ti mesmo

                int repNumber = i + 1;
                String replica_path = path + "/" + repNumber;

                System.out.println( replicaId +"\n" +replica_path);

                ZKRecord record = zkNaming.lookup(replica_path);

                String target = record.getURI();

                channels.put(repNumber, ManagedChannelBuilder.forTarget(target).usePlaintext().build());

                System.out.println(channels.size() + "\n" + i);

                stubMap.put(repNumber, SiloGrpc.newBlockingStub(channels.get(repNumber)));

                System.out.println("Replica Manager: " + replicaId + " connected to replica number : " + repNumber );

            }

        } catch (ZKNamingException e) {
            e.printStackTrace();
        }
    }

    public void update(){
        if (!connected){
            connectReplica(); connected = true;
        }

        List<SiloOuterClass.Camera> gossipCamList = new ArrayList<>();
        for (Camera c: camLog){
            SiloOuterClass.Camera gossipCam = SiloOuterClass.Camera.newBuilder().setName(c.getName()).setLatitude(c.getLatitude())
                    .setLongitude(c.getLongitude()).build();
            gossipCamList.add(gossipCam);
        }

        List<SiloOuterClass.ObservationLog> gossipObsList = new ArrayList<>();
        for (Observation o: obsLog){
            SiloOuterClass.ObservationLog gossipObs = SiloOuterClass.ObservationLog.newBuilder().setCam(o.getCamera())
                    .setType(o.getType()).setId(o.getId()).setTimestamp(o.getTimestamp().toString()).build();
            gossipObsList.add(gossipObs);
        }

        updateLog log = updateLog.newBuilder().addAllCamLog(gossipCamList).addAllReportLog(gossipObsList).build();
        GossipMessage gossip = GossipMessage.newBuilder().addAllTs(replicaTS).setLog(log).build();


        for (Map.Entry<Integer, SiloBlockingStub> stub: stubMap.entrySet()){
            //System.out.println("Replica: " + stub.getKey());
            stub.getValue().gossip(gossip);
        }

        if (replicaNro == 1){
            Ops.stable(obsLog, camLog, replicaTS);
            obsLog.clear();
            camLog.clear();
            for (int i = 0; i < replicaTS.size(); i++){
                replicaTS.set(i, 0);
            }
        }

    }

    public List<Integer> getTimeStampVector() {
        return replicaTS;
    }

    public void tsVectorPP(int replica){

        //System.out.println("tsVectorPP replica: " + replica);
        int newTs = replicaTS.get(replica - 1) + 1;
        //System.out.println("newTS: "+ newTs + "\nOldTs: " + timeStampVector.get(replica));
        replicaTS.set(replica - 1, newTs);
    }

    public void camJoin(String localName, String latitude, String longitude) throws BadEntryException {
        try {
            Camera cam =  Ops.camJoin(localName, latitude, longitude);
            System.out.println("replicaId: " + replicaId);
            tsVectorPP(replicaId);
            if (cam != null) {
                camLog.add(cam);
            }
        } catch (BadEntryException e){
            if (e.getErrorMessage().equals(ErrorMessage.CAM_COORDS_NOT_VALID)){
                throw new BadEntryException(ErrorMessage.CAM_COORDS_NOT_VALID);
            } else if (e.getErrorMessage().equals(ErrorMessage.CAM_NAME_NOT_VALID)){
                throw new BadEntryException(ErrorMessage.CAM_NAME_NOT_VALID);
            } else {
                throw new BadEntryException(ErrorMessage.CAM_NAME_ALREADY_EXIST);
            }
        }
    }

    public void report(List<String> camName, List<String> id, List<ObjectType> type) throws BadEntryException {
        try {
            List<Observation> list = Ops.report(camName, id, type);
            tsVectorPP(replicaId);

            for (Observation obs : list ){
                obsLog.add(obs);
            }

        } catch (BadEntryException e){
            throw new BadEntryException(ErrorMessage.ID_NOT_VALID);
        }
    }

    public void gossip(GossipMessage message){
        List<Integer> mTS = message.getTsList();
        System.out.println(mTS);

    }


    public Camera camInfo(String name){
        return Ops.camInfo(name);
    }

    public Observation track(ObjectType type, String id) throws BadEntryException {
        Observation o;
        try {
            o = Ops.track(type, id);
        } catch (BadEntryException e) {
            if (e.getErrorMessage().equals(ErrorMessage.ID_NOT_VALID)){
                throw new BadEntryException(ErrorMessage.ID_NOT_VALID);
            } else if (e.getErrorMessage().equals(ErrorMessage.ID_NOT_FOUND)){
                throw new BadEntryException(ErrorMessage.ID_NOT_FOUND);
            } else {
                throw new BadEntryException(ErrorMessage.ID_FOUND_WRONG_TYPE);
            }
        }
        return o;
    }

    public List<Observation> trackMatch(ObjectType type, String id) throws BadEntryException {
        List<Observation> oList;
        try{
            oList = Ops.trackMatch(type, id);
        } catch (BadEntryException e) {
            throw new BadEntryException(ErrorMessage.NO_ID_MATCH);
        }
        return oList;
    }

    public List<Observation> trace(ObjectType type, String id) throws BadEntryException {
        List<Observation> oList;
        try{
            oList = Ops.trace(type, id);
        } catch (BadEntryException e) {
            if (e.getErrorMessage().equals(ErrorMessage.ID_NOT_VALID)){
                throw new BadEntryException(ErrorMessage.ID_NOT_VALID);
            } else {
                throw new BadEntryException(ErrorMessage.NO_ID_MATCH);
            }
        }
        return oList;
    }


    public SiloServerOps getOps() {
        return Ops;
    }
}
