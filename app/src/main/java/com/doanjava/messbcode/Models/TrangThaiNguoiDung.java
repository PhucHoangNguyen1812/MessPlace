package com.doanjava.messbcode.Models;

import java.util.ArrayList;

public class TrangThaiNguoiDung {
    private  String nguoiDang, anhDaiDien;
    private String capNhatCuoi;
    private ArrayList<CauChuyen> cauChuyens;

    public TrangThaiNguoiDung() {
    }

    public TrangThaiNguoiDung(String nguoiDang, String anhDaiDien, String capNhatCuoi, ArrayList<CauChuyen> cauChuyens) {
        this.nguoiDang = nguoiDang;
        this.anhDaiDien = anhDaiDien;
        this.capNhatCuoi = capNhatCuoi;
        this.cauChuyens = cauChuyens;
    }

    public String getNguoiDang() {
        return nguoiDang;
    }

    public void setNguoiDang(String nguoidang) {
        this.nguoiDang = nguoidang;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }

    public String getCapNhatCuoi() {
        return capNhatCuoi;
    }

    public void setCapNhatCuoi(String capNhatCuoi) {
        this.capNhatCuoi = capNhatCuoi;
    }

    public ArrayList<CauChuyen> getCauChuyens() {
        return cauChuyens;
    }

    public void setCauChuyens(ArrayList<CauChuyen> cauChuyens) {
        this.cauChuyens = cauChuyens;
    }
}
