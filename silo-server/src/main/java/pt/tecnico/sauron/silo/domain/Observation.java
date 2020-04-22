package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.grpc.SiloOuterClass.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.Instant;
public class Observation  {


    private String id ;
    private ObjectType obsType;
    private Instant timestamp;
    private String camera;

    public Observation(ObjectType type, String id, String cam, Instant inst) {
        this.obsType = type;
        this.id = id;
        this.camera = cam;
        this.timestamp = inst;

    }

    public String getId(){ return this.id; }

    public ObjectType getType(){ return this.obsType; }

    public String getCamera(){ return this.camera; }

    public Instant getTimestamp(){ return this.timestamp; }

    public boolean equalType(ObjectType type){
        return this.obsType.equals(type);
    }


}
