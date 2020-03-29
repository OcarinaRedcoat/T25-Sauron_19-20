package domain;

public class Location {


    private float latitude;
    private float longitude;

    public Location(float x, float y) {

        this.latitude = x;
        this.longitude = y;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
