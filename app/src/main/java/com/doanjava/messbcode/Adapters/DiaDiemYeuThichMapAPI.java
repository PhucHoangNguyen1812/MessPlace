package com.doanjava.messbcode.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Models.DiaDiemYeuThich;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.R;

public class DiaDiemYeuThichMapAPI extends ArrayAdapter<DiaDiemYeuThich> {
    Activity context;
    int resource;
    public DiaDiemYeuThichMapAPI(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = (Activity) context;
        this.resource = resource;

    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater LayoutInflater = this.context.getLayoutInflater();
        View view = LayoutInflater.inflate(this.resource, null);
        ImageView img_Source = view.findViewById(R.id.viewAnhDaiDien);
        TextView ten = view.findViewById(R.id.txtTen);
        DiaDiemYeuThich user = getItem(position);
        Glide.with(context).load(user.getAnhDiaDiem())
                .placeholder(R.drawable.anhdd)
                .into(img_Source);
        ten.setText(user.getTenDiaDiem());
        return view;
    }

}

