package com.doanjava.messbcode.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Activities.KichHoatSoDienThoai;
import com.doanjava.messbcode.Activities.MainActivity;
import com.doanjava.messbcode.Activities.ThemBanBe;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.TinNhan;
import com.doanjava.messbcode.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ThemNguoiDung extends ArrayAdapter<NguoiDung> {
    Activity context;
    int resource,trangthai;
    String maNguoiGui;
    ArrayList<String> dsBanBe;
    ArrayList<String> dsCho;
    ArrayList<String> dsChapNhanYC;
    public ThemNguoiDung(@NonNull Context context, int resource, String maNguoiGui ,int trangthai,ArrayList<String> dsbanbe,ArrayList<String> dsCho, ArrayList<String> dsChapNhanYC) {
        super(context, resource);
        this.context = (Activity) context;
        this.resource = resource;
        this.maNguoiGui = maNguoiGui;
        this.trangthai = trangthai;
        this.dsBanBe = dsbanbe;
        this.dsCho = dsCho;
        this.dsChapNhanYC = dsChapNhanYC;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater LayoutInflater = this.context.getLayoutInflater();
        View view = LayoutInflater.inflate(this.resource, null);
        ImageView img_Source = view.findViewById(R.id.imgSource);
        TextView gioiTinh = view.findViewById(R.id.txtGioiTinh);
        TextView ten = view.findViewById(R.id.txtTen);
        NguoiDung user = getItem(position);
        TextView tinhTrang = view.findViewById(R.id.txtTinhTrang);
        Glide.with(context).load(user.getAnhDaiDien())
                .placeholder(R.drawable.avatar)
                .into(img_Source);
        ten.setText(user.getTen());
        gioiTinh.setText(user.getGioiTinh());
        if(trangthai == 0){
            if (dsBanBe.contains(user.getMaSo()))
                tinhTrang.setText("Bạn Bè");
            else if(dsCho.contains(user.getMaSo()))
                tinhTrang.setText("Đang Gửi Yêu Cầu");
            else if(dsChapNhanYC.contains(user.getMaSo()))
                tinhTrang.setText("Chấp Nhận Yêu Cầu");
            else
                tinhTrang.setText("Người Lạ");
        }
        return view;
    }

}

