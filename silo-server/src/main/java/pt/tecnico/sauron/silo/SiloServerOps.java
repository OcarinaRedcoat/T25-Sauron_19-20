package pt.tecnico.sauron.silo;

import domain.Location;

import java.util.HashMap;
import java.util.Map;


//private ArrayList <Pair <String, Location> > camsList = new ArrayList <Pair <String,Integer> > ();
//camsList.add(new Pair <String, Location> (name, cam_location));



public class SiloServerOps {

    private Map<String, Location> camsList = new HashMap<>();

    public SiloServerOps() {}

    public String camJoin(String name, float locationX, float locationY) {

        Location cam_location = new Location(locationX, locationY);
        camsList.put(name, cam_location);

        return "CAM_NAME:" + name + "CAM_LOCATION" + locationX + ":" + locationY;
    }

    public Location camInfo(String name) {

        for (Map.Entry<String, Location> entry : camsList.entrySet()) {
//            name is not an object
            if (entry.getKey().equals(name)) {
                return entry.getValue();
            }
        }
        System.out.println("No such camera exists");
        return null;
    }

}
