package pt.tecnico.sauron.silo.domain;

public class Camera {

    private String name;
    private float latitude;
    private float longitude;

    public Camera(String name, float latitude, float longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName(){ return name; }

    public float getLatitude(){ return latitude; }

    public float getLongitude(){ return longitude; }
}
