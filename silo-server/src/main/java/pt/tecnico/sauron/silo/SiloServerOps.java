package pt.tecnico.sauron.silo;

import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.exception.BadEntryException;

import java.util.HashMap;
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

    public void track(){}

    public void trackMatch(){}

    public void trace(){}

}
