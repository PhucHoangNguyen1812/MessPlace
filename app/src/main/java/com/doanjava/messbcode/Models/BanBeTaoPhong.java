package com.doanjava.messbcode.Models;

import java.io.Serializable;

public class BanBeTaoPhong implements Serializable {
    private String maso;
    private String Ten;
    private String chucvu = "";
    private boolean check;

    public BanBeTaoPhong(){}

    public BanBeTaoPhong(String maso, String ten, boolean check) {
        this.maso = maso;
        this.Ten = ten;
        this.check = check;
    }

    public void setChucvu(String chucvu) {
        this.chucvu = chucvu;
    }

    public String getMaso() {
        return maso;
    }

    public void setMaso(String maso) {
        this.maso = maso;
    }

    public String getTen() {
        return Ten;
    }

    public void setTen(String ten) {
        Ten = ten;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "("+ this.maso+") " + this.Ten + " " + chucvu;
    }
}
