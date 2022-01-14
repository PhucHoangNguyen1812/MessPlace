package com.doanjava.messbcode.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Activities.MainActivity;
import com.doanjava.messbcode.Activities.NhanTin;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChuyenDoiNguoiDung extends RecyclerView.Adapter<ChuyenDoiNguoiDung.CheDoXemNguoiDung> {
    Context context;
    ArrayList<NguoiDung> nguoiDungs;
    NguoiDung NguoiGui;
    public ChuyenDoiNguoiDung(Context context, NguoiDung NguoiGui, ArrayList<NguoiDung> nguoiDungs) {
        this.context = context;
        this.nguoiDungs = nguoiDungs;
        this.NguoiGui = NguoiGui;
    }

    @NonNull
    @Override
    public CheDoXemNguoiDung onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation,parent, false );

        return new CheDoXemNguoiDung(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull CheDoXemNguoiDung holder, int position) {
        NguoiDung nguoiDung = nguoiDungs.get(position);
        FirebaseDatabase.getInstance().getReference()
                .child("TroChuyen")
                .child(NguoiGui.getMaSo())
                .child(nguoiDung.getMaSo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("tinCuoi").exists()) {
                            String tinCuoi = snapshot.child("tinCuoi").getValue().toString();
                            String ngaygio = snapshot.child("thoiGianTinCuoi").getValue().toString();
                            holder.binding.msgTime.setText(ngaygio);
                            holder.binding.lastMsg.setText(tinCuoi);
                        } else {
                            holder.binding.lastMsg.setText("Nhấn để trò chuyên");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.username.setText(nguoiDung.getTen());

        Glide.with(context).load(nguoiDung.getAnhDaiDien())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NhanTin.class);
                intent.putExtra("NguoiNhan",nguoiDung);
                intent.putExtra("NguoiGui",NguoiGui);
                intent.putExtra("ten",NguoiGui.getTen());
                intent.putExtra("hinh",NguoiGui.getAnhDaiDien());
                intent.putExtra("token",nguoiDung.getToken());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return nguoiDungs.size();
    }

    public  class CheDoXemNguoiDung extends RecyclerView.ViewHolder {

        RowConversationBinding binding;

        public CheDoXemNguoiDung(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
