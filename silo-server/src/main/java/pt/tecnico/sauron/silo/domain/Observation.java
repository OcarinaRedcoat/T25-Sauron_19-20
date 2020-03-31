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

    public Observation(String type, String id, Camera cam){
        if (type.equals("person")){
            this.obsType = Type.PERSON;
        } else {
            this.obsType = Type.CAR;
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


}
