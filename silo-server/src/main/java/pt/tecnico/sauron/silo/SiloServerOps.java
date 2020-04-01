package pt.tecnico.sauron.silo;

import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class SiloServerOps {

    private Map<String, Camera> camsMap = new HashMap<>();
    private Map<String, Observation> obsMap = new HashMap<>();

    public SiloServerOps() {}






    public String camJoin(String name, float locationX, float locationY) throws IllegalArgumentException {
        Camera newCamera;

        if (name.matches("[A-Za-z0-9]+") && name.length() >= 3 && name.length() <= 15) {


            if (camsMap.get(name) == null) {
                newCamera = new Camera(name, locationX, locationY);
                camsMap.put(name, newCamera);
            }

            return "CAM_NAME:" + name + "CAM_LOCATION" + locationX + ":" + locationY;
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

        if (obsMap.get(id) == null){
            Observation obs = new Observation(type, id, camName);
            obsMap.put(id, obs);
        }

    }

    public Observation track(ObjectType type, String id) throws IllegalArgumentException{
        Observation obs = obsMap.get(id);
        if (!obs.equals(type)){
            throw new IllegalArgumentException("Id exists but wrong type");
        } else if (obs == null){
            throw new IllegalArgumentException("Id doesnt exist");
        }
        return obs;
    }

    public List<Observation> trackMatch(String type, String partId) throws IllegalArgumentException {

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
            throw new IllegalArgumentException("No lst, so somthing wrong is not right");
        }
        return lst;
    }

    public List<Observation> trace(String type, String id) throws IllegalArgumentException {
        List<Observation> obsLst = new ArrayList<>();
        return obsLst;
    }

}
