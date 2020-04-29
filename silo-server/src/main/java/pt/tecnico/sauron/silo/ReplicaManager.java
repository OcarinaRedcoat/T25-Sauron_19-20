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

    private String zooHost;
    private String zooPort;

    private Map<Integer, SiloBlockingStub> stubMap = new HashMap();
    private Map<Integer, ManagedChannel> channels = new HashMap();

    private List<Integer> timeStampVector;

    private SiloServerOps Ops = new SiloServerOps();

    public ReplicaManager(String path, String zooHost, String zooPort, int repNro, String id){
        this.path = path;
        this.replicaNro = repNro; // total number of replicas
        this.replicaId = Integer.parseInt(id);      // replica id

        zkNaming = new ZKNaming(zooHost, zooPort);

        timeStampVector = new ArrayList<>(Collections.nCopies(repNro, 0)); // initialize the timestamp vector with the number of replicas
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

        /*System.out.println();
        for (int i = 0 ;i < timeStampVector.size(); i++ ){
            System.out.println("Replica = " + i +" timeStamp = " + timeStampVector.get(i));
        }*/

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
        GossipMessage gossip = GossipMessage.newBuilder().addAllTs(timeStampVector).setLog(log).build();


        for (Map.Entry<Integer, SiloBlockingStub> stub: stubMap.entrySet()){
            //System.out.println("Replica: " + stub.getKey());
            stub.getValue().gossip(gossip);
        }


    }

    public List<Integer> getTimeStampVector() {
        return timeStampVector;
    }

    public void tsVectorPP(int replica){

        //System.out.println("tsVectorPP replica: " + replica);
        int newTs = timeStampVector.get(replica - 1) + 1;
        //System.out.println("newTS: "+ newTs + "\nOldTs: " + timeStampVector.get(replica));
        timeStampVector.set(replica - 1, newTs);
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

}
