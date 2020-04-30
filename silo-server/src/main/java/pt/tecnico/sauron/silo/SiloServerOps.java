package pt.tecnico.sauron.silo;

import com.google.protobuf.Internal;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.exceptions.BadEntryException;
import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass.*;
import java.lang.NumberFormatException;


import java.time.Instant;
import java.util.*;


public class SiloServerOps {

    private Map<String, Camera> camsMap = new HashMap<>();
    private Map<String, Observation> obsMap = new HashMap<>();

    private List<Observation> allObservations = new ArrayList<>();

    private List<Integer> valueTS;

    public SiloServerOps() {}

    public SiloServerOps(int replicaNro) {
        valueTS = new ArrayList<>(Collections.nCopies(replicaNro, 0)); // initialize the timestamp vector with the number of replicas
    }

    public synchronized String ping(String ping) throws BadEntryException{
        if (ping == null || ping.isEmpty()){
            throw new BadEntryException(ErrorMessage.EMPTY_PING);
        }
        return ping;
    }

    public synchronized void init(){} // We initalize everything when new

    // For ctrl_clear
    public synchronized void clearAll() {
        camsMap.clear();;
        obsMap.clear();
        allObservations.clear();
    }


    public synchronized boolean checkArgs(String id, SiloOuterClass.ObjectType type) {

        if(type.equals(ObjectType.person)) {
            try {
                Long.parseLong(id);
                if (Long.parseLong(id) >= 0) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        else if(type.equals(ObjectType.car)) {
            if(id.length() != 6) {
                return false;
            }
            String s1 = id.substring(0,2);
            String s2 = id.substring(2,4);
            String s3 = id.substring(4,6);
            if (checkCarID(s1, s2, s3)) {
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    public synchronized boolean checkCarID(String s1, String s2, String s3) {
        int num = 0;
        int letter = 0;

        if (s1.matches("[A-Z]+")) { letter++; }
        if (s2.matches("[A-Z]+")) { letter++; }
        if (s3.matches("[A-Z]+")) { letter++; }
        if (s1.matches("[0-9]+")) { num++; }
        if (s2.matches("[0-9]+")) { num++; }
        if (s3.matches("[0-9]+")) { num++; }
        if ((num == 2 && letter == 1) || (num == 1 && letter == 2)) { return true; }
        else { return false; }
    }




    public synchronized Camera camJoin(String name, String locationX, String locationY) throws BadEntryException {
        Camera newCamera;
        float longitude;
        float latitude;

        try {
            latitude = Float.parseFloat(locationX);
            longitude = Float.parseFloat(locationY);
        } catch (NumberFormatException nfe) {
            throw new BadEntryException(ErrorMessage.CAM_COORDS_NOT_VALID);
        }

        if (camsMap.get(name) == null) {

            if (name.matches("[A-Za-z0-9]+") && name.length() >= 3 && name.length() <= 15) {


                if (camsMap.get(name) == null) {
                    newCamera = new Camera(name, latitude, longitude);
                    camsMap.put(name, newCamera);
                    return newCamera;
                }

            } else {
                throw new BadEntryException(ErrorMessage.CAM_NAME_NOT_VALID);
            }

        } else{

            Camera cam = camsMap.get(name);
            if ((cam.getLongitude() != longitude) || (cam.getLatitude() != latitude)){
                System.out.println("Camara no servidor: " + cam.getLongitude() + " " + cam.getLatitude());
                System.out.println("Camara input: " + longitude + " " + latitude);
                throw new BadEntryException(ErrorMessage.CAM_NAME_ALREADY_EXIST);
            }

        }

        System.out.println("new cam: " + name + " lat: " + latitude + " long: " + longitude);
        return null; // voltou a entrar uma camera ja existente no server
    }


    public synchronized Camera camInfo(String name) {

        return camsMap.get(name);
    }

    public synchronized List<Observation> report(List<String> camName, List<String> id, List<ObjectType> type) throws BadEntryException {
        if (id.size() != type.size()){
            System.out.println("Algo de errado não está certo no tamanho das listas: report SiloServerOps");
        }

        Instant instantLot = Instant.now();

        List<Observation> listRes = new ArrayList<>();
        for (int i = 0; i < id.size(); i++) {
            if (!checkArgs(id.get(i), type.get(i))) {
                System.out.println(ErrorMessage.ID_NOT_VALID);
                continue;
            }
            Observation obs = new Observation(type.get(i), id.get(i), camName.get(i), instantLot);
            obsMap.put(id.get(i), obs);
            allObservations.add(obs);
            listRes.add(obs);
        }

        return listRes;
    }

    public synchronized Observation track(ObjectType type, String id) throws BadEntryException{

        if (!checkArgs(id, type)){
            throw new BadEntryException(ErrorMessage.ID_NOT_VALID);
        }

        Observation obs = obsMap.get(id);

        if (obs == null){
            throw new BadEntryException(ErrorMessage.ID_NOT_FOUND);
        }
        if (!obs.equalType(type)){
            throw new BadEntryException(ErrorMessage.ID_FOUND_WRONG_TYPE);
        }
        return obs;
    }

    public synchronized List<Observation> trackMatch(ObjectType type, String partId) throws BadEntryException {

        List<Observation> lst = new ArrayList<>();

        if (partId.equals("*")){
            for (Observation o: obsMap.values()){
                if (o.equalType(type)) {
                    lst.add(o);
                }
            }
        } else if(partId.startsWith("*")){
            for (Observation o: obsMap.values()) {
                if (o.getId().endsWith(partId.substring(1)) && o.equalType(type)){
                    lst.add(o);
                }
            }
        } else if (partId.endsWith("*")) {
            String part = partId.substring(0, partId.length() - 1);

            for (Observation o : obsMap.values()) {
                if (o.getId().startsWith(part) && o.equalType(type)) {
                    lst.add(o);
                }
            }
        }
        else {
            String[] parts = partId.split("\\*");
            String part1 = parts[0];
            String part2 = parts[1];


            for (Observation o : obsMap.values()) {
                if (o.getId().startsWith(part1) && o.getId().endsWith(part2) && o.equalType(type)) {
                    lst.add(o);
                }
            }


        }
        if (lst.isEmpty()){
            throw new BadEntryException(ErrorMessage.NO_ID_MATCH);
        }

        if (type.equals(ObjectType.person)){
            Collections.sort(lst, new Comparator<Observation>() {
                @Override
                public int compare(Observation o1, Observation o2) {
                    if (Long.parseLong(o1.getId()) > Long.parseLong(o2.getId())) {
                        return 1;
                    }
                    return -1;
                }
            });


        } else {
            Collections.sort(lst, new Comparator<Observation>() {
                @Override
                public int compare(Observation o1, Observation o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });

        }
        return lst;
    }




    public synchronized List<Observation> trace(ObjectType type, String id) throws BadEntryException {

        if (!checkArgs(id, type)){
            throw new BadEntryException(ErrorMessage.ID_NOT_VALID);
        }
        List<Observation> obsLst = new ArrayList<>();
        for (int i = allObservations.size(); i > 0; i--){
            Observation o = allObservations.get(i - 1);
            if (o.getId().equals(id) && o.equalType(type)){
                obsLst.add(o);
            }
        }
        if (obsLst.isEmpty()){
            throw new BadEntryException(ErrorMessage.NO_ID_MATCH);
        }
        return obsLst;
    }
/*
    public synchronized void stable(List<Observation> obsList, List<Camera> camList, List<Integer> replicaTS){

        for (Camera c: camList){
            camsMap.put(c.getName(), c);
        }

        System.out.println(obsList.size() + "size");

        for (Observation o : obsList) {
            if (obsMap.get(o.getId()) == null) {
                obsMap.put(o.getId(), o);
                System.out.println("id: " + o.getId());
            } else {
                if (obsMap.get(o.getId()).getTimestamp().isBefore(o.getTimestamp())) {
                    obsMap.put(o.getId(), o);
                }
            }
            allObservations.add(o);
            //System.out.println("Na lista 2");

        }

        for (int i = 0; i < valueTS.size(); i++){
            valueTS.set(i, valueTS.get(i) + replicaTS.get(i));
            System.out.println("valueTS: " + valueTS.get(i));
        }
        /*for (Observation o: obsList){
            if(obsMap.get(o.getId()) == null){
                obsMap.put(o.getId(), o);
                System.out.println("Mais recente");
            } else {
                if (obsMap.get(o.getId()).getTimestamp().isBefore(o.getTimestamp())){
                    obsMap.put(o.getId(), o);
                }
            }
            obsList.add(o);
            System.out.println("Na lista");
        }

    }
    */

    public void merge(List<SiloOuterClass.Camera> camlog, List<SiloOuterClass.ObservationLog> reportlog){
        for (SiloOuterClass.Camera c: camlog){
            Camera newCamera = new Camera(c.getName(), c.getLatitude(), c.getLongitude());
            camsMap.put(c.getName(), newCamera);
        }
        for (SiloOuterClass.ObservationLog o: reportlog){
            Observation newObs = new Observation(o.getType(), o.getId(), o.getCam(), Instant.parse(o.getTimestamp()));
            if(obsMap.get(newObs.getId()) == null){
                obsMap.put(newObs.getId(), newObs);
            } else {
                if (obsMap.get(newObs.getId()).getTimestamp().isBefore(newObs.getTimestamp())){
                    obsMap.put(newObs.getId(), newObs);
                }
            }
            allObservations.add(newObs);
        }

    }
}
