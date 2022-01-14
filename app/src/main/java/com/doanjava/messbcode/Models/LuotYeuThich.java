package com.doanjava.messbcode.Models;

import java.util.ArrayList;

public class LuotYeuThich {
    ArrayList<String> nguoiYeuThich;

    public LuotYeuThich(ArrayList<String> nguoiYeuThich) {
        this.nguoiYeuThich = nguoiYeuThich;
    }

    public LuotYeuThich(){}

    public ArrayList<String> getNguoiYeuThich() {
        return nguoiYeuThich;
    }

    public void setNguoiYeuThich(ArrayList<String> nguoiYeuThich) {
        this.nguoiYeuThich = nguoiYeuThich;
    }
}
