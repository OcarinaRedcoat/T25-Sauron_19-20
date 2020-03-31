package pt.tecnico.sauron.silo;

import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;

import java.util.HashMap;
import java.util.Map;



public class SiloServerOps {

    private Map<String, Camera> camsMap = new HashMap<>();
    private Map<String, Observation> obsMap = new HashMap<>();

    public SiloServerOps() {}






    public String camJoin(String name, float locationX, float locationY) {

        Camera newCamera = new Camera(name, locationX, locationY);
        camsMap.put(name, newCamera);

        return "CAM_NAME:" + name + "CAM_LOCATION" + locationX + ":" + locationY;
    }


    public Camera camInfo(String name) {

        Camera cam = camsMap.get(name);
        return cam;

    }

    public void report(String camName, String id, String type){

        Camera cam = camsMap.get(camName);
        if (obsMap.get(id) == null){
            Observation obs = new Observation(type, id, cam);
            obsMap.put(id, obs);
        } else {
            Observation obs = obsMap.get(id);
            obs.addCamera(cam);
        }

    }
}
