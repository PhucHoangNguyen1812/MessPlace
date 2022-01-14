package com.doanjava.messbcode.Models;

import java.io.Serializable;

public class DiaDiemGoiY implements Serializable {
    private String maDiaDiem;
    private String tenDiaDiem;
    private String anhDiaDiem;
    private double latitude;
    private double longitude;
    private int sum;

    public DiaDiemGoiY(String tenDiaDiem, String anhDiaDiem, double latitude, double longitude) {
        this.tenDiaDiem = tenDiaDiem;
        this.anhDiaDiem = anhDiaDiem;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getMaDiaDiem() {
        return maDiaDiem;
    }

    public void setMaDiaDiem(String maDiaDiem) {
        this.maDiaDiem = maDiaDiem;
    }

    public DiaDiemGoiY(){}

    public String getTenDiaDiem() {
        return tenDiaDiem;
    }

    public void setTenDiaDiem(String tenDiaDiem) {
        this.tenDiaDiem = tenDiaDiem;
    }

    public String getAnhDiaDiem() {
        return anhDiaDiem;
    }

    public void setAnhDiaDiem(String anhDiaDiem) {
        this.anhDiaDiem = anhDiaDiem;
    }

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
