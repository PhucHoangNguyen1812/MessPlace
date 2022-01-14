package com.doanjava.messbcode.Models;

public class CauChuyen {
    private String linkAnh;
    private String thoiGian;

    public CauChuyen() {
    }

    public CauChuyen(String linkAnh, String thoiGian) {
        this.linkAnh = linkAnh;
        this.thoiGian = thoiGian;
    }

    public String getLinkAnh() {
        return linkAnh;
    }

    public void setLinkAnh(String linkAnh) {
        this.linkAnh = linkAnh;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }
}
