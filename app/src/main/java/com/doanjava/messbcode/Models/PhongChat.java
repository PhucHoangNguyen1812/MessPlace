package com.doanjava.messbcode.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class PhongChat implements Serializable {
    private String maPhong;

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getMaPhong() {
        return maPhong;
    }

    private String tenPhong;
    private String linkAnh;
    private String nguoiTao;
    private ArrayList<String> quanTri;
    private ArrayList<String> thanhVien;
    private String token;
    public PhongChat(String tenPhong, String linkAnh, String nguoiTao, ArrayList<String> quanTri, ArrayList<String> thanhVien) {
        this.tenPhong = tenPhong;
        this.linkAnh = linkAnh;
        this.nguoiTao = nguoiTao;
        this.thanhVien = thanhVien;
        this.quanTri = quanTri;
    }

    public PhongChat(){}

    public ArrayList<String> getQuanTri() {
        return quanTri;
    }

    public void setQuanTri(ArrayList<String> quanTri) {
        this.quanTri = quanTri;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getLinkAnh() {
        return linkAnh;
    }

    public void setLinkAnh(String linkAnh) {
        this.linkAnh = linkAnh;
    }

    public String getNguoiTao() {
        return nguoiTao;
    }

    public void setNguoiTao(String nguoiTao) {
        this.nguoiTao = nguoiTao;
    }

    public ArrayList<String> getThanhVien() {
        return thanhVien;
    }

    public void setThanhVien(ArrayList<String> thanhVien) {
        this.thanhVien = thanhVien;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
