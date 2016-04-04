package com.github.changamire;

public class GeoLocation {

    private double latitude;
    private double longitude;

    public GeoLocation(double latitude, double longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
