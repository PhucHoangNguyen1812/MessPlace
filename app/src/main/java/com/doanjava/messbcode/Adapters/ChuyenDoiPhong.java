package com.doanjava.messbcode.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Activities.NhanTinPhongChat;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.PhongChat;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.RowConversationBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChuyenDoiPhong extends RecyclerView.Adapter<ChuyenDoiPhong.CheDoXemPhong> {

    Context context;
    ArrayList<PhongChat> phongChats;
    String maNguoiGui;
    NguoiDung nguoiGui;
    public ChuyenDoiPhong(Context context, String maNguoiGui, ArrayList<PhongChat> phongChats,NguoiDung nguoiGui ) {
        this.context = context;
        this.phongChats = phongChats;
        this.maNguoiGui = maNguoiGui;
        this.nguoiGui = nguoiGui;
    }

    @NonNull
    @Override
    public CheDoXemPhong onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation,parent, false );

        return new CheDoXemPhong(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull CheDoXemPhong holder, int position) {
        PhongChat phongchat = phongChats.get(position);

        FirebaseDatabase.getInstance().getReference()
                .child("PhongChat")
                .child(phongchat.getMaPhong())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("tinCuoi").exists()) {
                            String tinCuoi = snapshot.child("tinCuoi").getValue().toString();
                            String ngaygio = snapshot.child("thoiGianTinCuoi").getValue().toString();
                            holder.binding.msgTime.setText(ngaygio);
                                FirebaseDatabase.getInstance().getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            if(snapshot1.getKey().equals(maNguoiGui)) {
                                                NguoiDung ng = snapshot1.getValue(NguoiDung.class);
                                                if(tinCuoi.length() > ng.getTen().length() && ng.getTen().toUpperCase().equals((tinCuoi.toUpperCase()).substring(0,ng.getTen().length()))) {
                                                    holder.binding.lastMsg.setText("Bạn vừa tạo một phòng mới");
                                                    nguoiGui = ng;
                                                }
                                                else
                                                    holder.binding.lastMsg.setText(tinCuoi);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        } else {
                            holder.binding.lastMsg.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.username.setText("Phòng: " + phongchat.getTenPhong().toUpperCase());
        Glide.with(context).load(phongchat.getLinkAnh())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NhanTinPhongChat.class);
                intent.putExtra("maNguoiGui",maNguoiGui);
                intent.putExtra("PhongChat",phongchat);
                intent.putExtra("token",phongchat.getToken());
                intent.putExtra("ten",nguoiGui.getTen());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return phongChats.size();
    }

    public  class CheDoXemPhong extends RecyclerView.ViewHolder {

        RowConversationBinding binding;

        public CheDoXemPhong(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
