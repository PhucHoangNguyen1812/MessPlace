package com.doanjava.messbcode.Models;

public class ViTriGPS {
    public double latitude;
    public double longitude;

    public ViTriGPS(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ViTriGPS(){}

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
