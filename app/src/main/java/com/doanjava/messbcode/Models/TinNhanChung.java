package com.doanjava.messbcode.Models;

import com.doanjava.messbcode.Activities.NhanTin;

public class TinNhanChung {
    public double latitude;
    public double longitude;
    public NhanTin nhanTin;

    public TinNhanChung(double latitude, double longitude, NhanTin nhanTin) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.nhanTin = nhanTin;
    }

    public TinNhanChung(){}

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

    public NhanTin getNhanTin() {
        return nhanTin;
    }

    public void setNhanTin(NhanTin nhanTin) {
        this.nhanTin = nhanTin;
    }
}
