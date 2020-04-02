package pt.tecnico.sauron.silo;

import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass.*;
import java.lang.NumberFormatException;


import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class SiloServerOps {

    private Map<String, Camera> camsMap = new HashMap<>();
    private Map<String, Observation> obsMap = new HashMap<>();

    private List<Observation> allObservations = new ArrayList<>();

    public SiloServerOps() {}

    public boolean checkArgs(String id, SiloOuterClass.ObjectType type) {

        if(type.equals(ObjectType.person)) {
            try {
                Long.parseLong(id);
                return true;
            } catch (NumberFormatException e) {
                //FIXME Not long
                System.out.println("deu raia person");
                return false;
            }
        }
        else if(type.equals(ObjectType.car)) {
            String s1 = id.substring(0,2);
            String s2 = id.substring(2,4);
            String s3 = id.substring(4,6);
            if (checkCarID(s1, s2, s3)) {
                return true;
            }
            else {
                //FIXME adicionar excecao
                System.out.println("deu raia car");
                return false;
            }
        }
        return false;
    }

    public boolean checkCarID(String s1, String s2, String s3) {
        int num = 0;
        int letter = 0;

        if (s1.matches("[A-Z]+")) {
            letter++;
        }
        if (s2.matches("[A-Z]+")) {
            letter++;
        }
        if (s3.matches("[A-Z]+")) {
            letter++;
        }
        if (s1.matches("[0-9]+")) {
            num++;
        }
        if (s2.matches("[0-9]+")) {
            num++;
        }
        if (s3.matches("[0-9]+")) {
            num++;
        }
        if ((num == 2 && letter == 1) || (num == 1 && letter == 2)) {
            return true;
        }
        else {
            return false;
        }
    }




    public void camJoin(String name, float locationX, float locationY) throws IllegalArgumentException {
        Camera newCamera;

        if (name.matches("[A-Za-z0-9]+") && name.length() >= 3 && name.length() <= 15) {


            if (camsMap.get(name) == null) {
                newCamera = new Camera(name, locationX, locationY);
                camsMap.put(name, newCamera);
            }

        }

        else {
            throw new IllegalArgumentException("Name non alhpanumeric");
        }
    }


    public Camera camInfo(String name) {
        // FIXME neste momento retorna o nome e coords da camara
        return camsMap.get(name);
    }

    public void report(String camName, String id, ObjectType type) throws IllegalArgumentException {

        if(!checkArgs(id, type)) {
            return;
        }

        Observation obs = new Observation(type, id, camName);
        obsMap.put(id, obs);
        allObservations.add(obs);
        System.out.println("!!!" + obs.getId() + "!!!" + obs.getTimestamp().toString() + "!!!" + obs.getType().toString() + "!!!" + obs.getCamera() + "!!!");
    }

    public Observation track(ObjectType type, String id) throws IllegalArgumentException{

        checkArgs(id, type);

        Observation obs = obsMap.get(id);
        if (!obs.equalType(type)){
            throw new IllegalArgumentException("Id exists but wrong type");
        } else if (obs == null){
            throw new IllegalArgumentException("Id doesnt exist");
        }
        return obs;
    }

    public List<Observation> trackMatch(ObjectType type, String partId) throws IllegalArgumentException {

        List<Observation> lst = new ArrayList<>();
        if (partId.startsWith("*")){
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
            throw new IllegalArgumentException("No lst, so something wrong is not right");
        }
        return lst;
    }




    public List<Observation> trace(ObjectType type, String id) throws IllegalArgumentException {

        checkArgs(id, type);

        List<Observation> obsLst = new ArrayList<>();
        for (Observation o: allObservations) {
            if (o.getId().equals(id) && o.equalType(type)){
                obsLst.add(o);
            }
        }
        if (obsLst.isEmpty()){
            throw new IllegalArgumentException("No lst, so something wrong is not right");
        }
        return obsLst;
    }

}
