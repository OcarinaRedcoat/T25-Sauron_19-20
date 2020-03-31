package pt.tecnico.sauron.silo;

import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.exception.BadEntryException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class SiloServerOps {

    private Map<String, Camera> camsMap = new HashMap<>();
    private Map<String, Observation> obsMap = new HashMap<>();

    public SiloServerOps() {}






    public String camJoin(String name, float locationX, float locationY) throws BadEntryException {
        Camera newCamera;

        if (name.matches("[A-Za-z0-9]+") && name.length() >= 3 && name.length() <= 15) {


            if (camsMap.get(name) == null) {
                newCamera = new Camera(name, locationX, locationY);
                camsMap.put(name, newCamera);
            }

            return "CAM_NAME:" + name + "CAM_LOCATION" + locationX + ":" + locationY;
        }

        else {
            throw new BadEntryException("Name non alhpanumeric");
        }
    }


    public Camera camInfo(String name) {
        // FIXME neste momento retorna o nome e coords da camara
        return camsMap.get(name);
    }

    public void report(String camName, String id, String type) throws BadEntryException {

        Camera cam = camsMap.get(camName);
        if (obsMap.get(id) == null){
            Observation obs = new Observation(type, id, cam);
            obsMap.put(id, obs);
        } else {
            Observation obs = obsMap.get(id);
            obs.addCamera(cam);
        }

    }

    public String track(String type, String id){
        Observation obs = obsMap.get(id);
        return obs.toStringRecent(type);
    }

    public List<String> trackMatch(String type, String partId) throws BadEntryException {
        List<String> lst = new ArrayList<>();
        if (partId.startsWith("*")){
            for (Observation o: obsMap.values()) {
                if (o.getId().endsWith(partId.substring(1)) && o.equalType(type)){
                    lst.add(o.toStringRecent(type));
                }
            }
        } else if (partId.endsWith("*")) {
            String part = partId.substring(0, partId.length() - 1);

            for (Observation o : obsMap.values()) {
                if (o.getId().startsWith(part) && o.equalType(type)) {
                    lst.add(o.toStringRecent(type));
                }
            }
        }
        else {
            String[] parts = partId.split("\\*");
            String part1 = parts[0];
            String part2 = parts[1];


            for (Observation o : obsMap.values()) {
                if (o.getId().startsWith(part1) && o.getId().endsWith(part2) && o.equalType(type)) {
                    lst.add(o.toStringRecent(type));
                }
            }


        }
        if (lst.isEmpty()){
            throw new BadEntryException("No lst, so somthing wrong is not right");
        }
        return lst;
    }

    public List<String> trace(String type, String id) throws BadEntryException {
        Observation obs = obsMap.get(id);
        List<String> lst = obs.toStringAll(type);
        if (!obs.equalType(type)){
            throw new BadEntryException("Wrong Type");
        } else if (lst.isEmpty()){
            throw new BadEntryException("Lst empty");
        }
        return lst;
    }

}
