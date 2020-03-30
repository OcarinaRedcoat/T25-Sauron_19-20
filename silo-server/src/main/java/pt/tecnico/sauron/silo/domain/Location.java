package pt.tecnico.sauron.silo.domain;

public class Location {


    private float latitude;
    private float longitude;

    public Location(float x, float y) {

        this.latitude = x;
        this.longitude = y;
    }

    public float getLatitudeFromDomain() {
        return latitude;
    }

    public float getLongitudeFromDomain() {
        return longitude;
    }
}
