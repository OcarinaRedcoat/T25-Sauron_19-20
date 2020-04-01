package pt.tecnico.sauron.silo.domain;


import java.util.ArrayList;
import java.util.List;
import java.time.Instant;

public class Observation {

    private String id;
    private List<Camera> camLst;
    private List<Instant> timeLst;
    private Type obsType;
    enum Type {
        CAR, PERSON
    }

    public Observation(String type, String id, Camera cam) throws IllegalArgumentException {
        if (type.equals("person")){
            this.obsType = Type.PERSON;
        } else if (type.equals("car")){
            this.obsType = Type.CAR;
        }else{
            throw new IllegalArgumentException("Type not valid");
        }
        this.id = id;
        camLst = new ArrayList<>();
        camLst.add(cam);
        timeLst = new ArrayList<>();
        timeLst.add(Instant.now());
    }

    public void addCamera(Camera cam){
        camLst.add(cam);
        timeLst.add(Instant.now());
    }

    public String getId(){ return id; }

    public List<Camera> getCamLst(){ return camLst; }

    public List<Instant> getTimeLst(){ return timeLst; }

    public Observation.Type getObsType(){ return obsType; }

    public String toStringRecent(String type){

        Camera cam = this.getCamLst().get(this.getCamLst().size()-1); // last item of the list
        Instant inst = this.getTimeLst().get(this.getTimeLst().size()-1); // last item of the list

        return type + "," + this.getId() + "," + inst.toString() + "," + "," + cam.getName() + "," + cam.getLatitude() + "," + cam.getLongitude();
    }

    public boolean equalType(String type){
        if (type.equals("person") && this.obsType == Type.PERSON){
            return true;
        } else if (type.equals("car") && this.obsType == Type.CAR){
            return true;
        }
        return false;
    }

    public List<String> toStringAll(String type){
        List<String> lst = new ArrayList<>();
        for (int i = 0; i < getCamLst().size(); i++){
            Camera cam = this.getCamLst().get(i);
            String str = type + "," + this.getId() + "," + timeLst.get(i).toString() + "," + "," + cam.getName() + "," + cam.getLatitude() + "," + cam.getLongitude();
        }
        return lst;
    }

}
