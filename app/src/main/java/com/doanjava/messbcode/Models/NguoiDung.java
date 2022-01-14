package com.doanjava.messbcode.Models;

import java.io.Serializable;
import java.util.Date;

public class NguoiDung implements Serializable {

    private String maSo, ten, email, matKhau, gioiTinh, anhDaiDien;
    private String ngaySinh;
    private String soDienThoai;
    private String token;
    private double latitude;
    private double longitude;

    public NguoiDung() {

    }

    public NguoiDung(String maSo, String ten, String email, String matKhau, String gioiTinh, String anhDaiDien, String ngaySinh, String soDienThoai, double latitude, double longitude) {
        this.maSo = maSo;
        this.ten = ten;
        this.email = email;
        this.matKhau = matKhau;
        this.gioiTinh = gioiTinh;
        this.anhDaiDien = anhDaiDien;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getMaSo() {
        return maSo;
    }

    public void setMaSo(String maSo) {
        this.maSo = maSo;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}