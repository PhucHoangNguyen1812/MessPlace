package com.doanjava.messbcode.Adapters;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Activities.GoiYDiaDiem;
import com.doanjava.messbcode.Models.DiaDiemGoiY;
import com.doanjava.messbcode.R;

import java.io.IOException;
import java.util.ArrayList;

public class ChuyenDoiGoiYDiaDiem extends ArrayAdapter<DiaDiemGoiY> {
    Activity context;
    int resource;
    String maNguoiGui;
    public ChuyenDoiGoiYDiaDiem(@NonNull Context context, int resource, String maNguoiGui ) {
        super(context, resource);
        this.context = (Activity) context;
        this.resource = resource;
        this.maNguoiGui = maNguoiGui;

    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater LayoutInflater = this.context.getLayoutInflater();
        View view = LayoutInflater.inflate(this.resource, null);
        ImageView img_Source = view.findViewById(R.id.imgSource);
        TextView gioiTinh = view.findViewById(R.id.txtGioiTinh);
        TextView ten = view.findViewById(R.id.txtTen);
        DiaDiemGoiY diadiem = getItem(position);
        TextView tinhTrang = view.findViewById(R.id.txtTinhTrang);
        Glide.with(context).load(diadiem.getAnhDiaDiem())
                .placeholder(R.drawable.avatar)
                .into(img_Source);
        ten.setText(diadiem.getTenDiaDiem());
        Geocoder geocoder = new Geocoder(context);
        ArrayList<Address> addressList = null;
        try {
            addressList = (ArrayList<Address>) geocoder.getFromLocation(diadiem.getLatitude(),diadiem.getLongitude(),1);
            Address address = addressList.get(0);
            if(address != null)
                gioiTinh.setText(address.getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tinhTrang.setText(diadiem.getSum() + "");
        return view;
    }

}




