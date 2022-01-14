package com.doanjava.messbcode.Models;

public class TinNhan {
    private String maTin, tinNhan, maNguoiGui,linkAnhGui;
    private String thoiGianTin;
    private int camXuc = -1 ;

    public TinNhan() {

    }

    public TinNhan(String tinNhan, String maNguoiGui, String thoiGianTin) {
        this.tinNhan = tinNhan;
        this.maNguoiGui = maNguoiGui;
        this.thoiGianTin = thoiGianTin;
    }

    public String getMaTin() {
        return maTin;
    }

    public void setMaTin(String maTin) {
        this.maTin = maTin;
    }

    public String getTinNhan() {
        return tinNhan;
    }

    public void setTinNhan(String tinNhan) {
        this.tinNhan = tinNhan;
    }

    public String getMaNguoiGui() {
        return maNguoiGui;
    }

    public void setMaNguoiGui(String maNguoiGui) {
        this.maNguoiGui = maNguoiGui;
    }

    public String getThoiGianTin() {
        return thoiGianTin;
    }

    public void setThoiGianTin(String thoiGianTin) {
        this.thoiGianTin = thoiGianTin;
    }

    public int getCamXuc() {
        return camXuc;
    }

    public void setCamXuc(int camXuc) {
        this.camXuc = camXuc;
    }

    public String getLinkAnhGui() {
        return linkAnhGui;
    }

    public void setLinkAnhGui(String linkAnhGui) {
        this.linkAnhGui = linkAnhGui;
    }
}
