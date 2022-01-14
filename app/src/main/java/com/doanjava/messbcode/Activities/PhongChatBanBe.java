package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.doanjava.messbcode.Adapters.ChuyenDoiPhong;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.PhongChat;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityPhongChatBanBeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

public class PhongChatBanBe extends AppCompatActivity {

    ActivityPhongChatBanBeBinding binding;
    FirebaseDatabase database;
    ArrayList<PhongChat> phongChats = new ArrayList<>();
    ChuyenDoiPhong chuyenDoiPhong;
    ProgressDialog dialog;
    ArrayList<Address> addressList = new ArrayList<>();
    NguoiDung nguoiDung = new NguoiDung();
    int max;
    String maso;
    public String Setmaso()
    {
        maso = getIntent().getStringExtra("maso");
        return maso;
    }

    public String BoDauCach(String chuoi)
    {
        String[] mang = chuoi.split(" ");
        chuoi = "";
        for(int i = 0;i<mang.length;i++){
            chuoi += mang[i];
        }
        return chuoi;
    }

    public void TimKiemList() {
        String chuoi ;
        chuoi = BoDauCach(binding.txtSearchPhongChat.getText().toString().toUpperCase());
        if(chuoi == null || chuoi.equals(""))
            chuyenDoiPhong = new ChuyenDoiPhong(PhongChatBanBe.this,maso, phongChats,nguoiDung);
        else
        {
            ArrayList<PhongChat> timkiems = new ArrayList<>();
            for (PhongChat phongChat:phongChats) {
                if(BoDauCach(phongChat.getTenPhong().toUpperCase()).contains(chuoi)){
                    timkiems.add(phongChat);
                }
            }
            chuyenDoiPhong = new ChuyenDoiPhong(PhongChatBanBe.this,maso, timkiems,nguoiDung);
        }
        binding.recyclerView.setAdapter(chuyenDoiPhong);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhongChatBanBeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        maso = Setmaso();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.txtSearchPhongChat.setVisibility(View.GONE);
        maso = Setmaso();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        chuyenDoiPhong = new ChuyenDoiPhong(PhongChatBanBe.this,maso, phongChats,nguoiDung);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(PhongChatBanBe.this));
        binding.recyclerView.setAdapter(chuyenDoiPhong);
        binding.recyclerView.showShimmerAdapter();
        String maHoatDong = "NguoiDung " + maso;

        //Lấy DS Phòng Chat
        database.getReference().child("PhongChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phongChats.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        PhongChat phongChat = snapshot1.getValue(PhongChat.class);
                        phongChat.setMaPhong(snapshot1.getKey());
                        if(phongChat.getQuanTri() != null)
                            if (phongChat.getQuanTri().contains(maso)) {
                                phongChats.add(phongChat);
                        }

                        if(phongChat.getThanhVien() != null)
                            if (phongChat.getThanhVien().contains(maso)) {
                                phongChats.add(phongChat);
                            }

                    }
                    binding.recyclerView.hideShimmerAdapter();
                    binding.txtSearchPhongChat.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            TimKiemList();
                            return false;
                        }
                    });
                    chuyenDoiPhong.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Trực tuyến");

    }

    @Override
    protected void onPause() {
        super.onPause();
        maso = Setmaso();
        String maHoatDong ="NguoiDung " + maso;
        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Ngoại tuyến");

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nhom:
                maso = Setmaso();
                Intent intent = new Intent(PhongChatBanBe.this, NhanTinDiaDiem.class);
                intent.putExtra("maNguoiGui",maso);
                startActivity(intent);
                break;
            case R.id.timkiemphong:
                if(binding.txtSearchPhongChat.getVisibility() == View.GONE)
                    binding.txtSearchPhongChat.setVisibility(View.VISIBLE);
                else
                    binding.txtSearchPhongChat.setVisibility(View.GONE);
                break;
            case R.id.taophong:
                maso = Setmaso();
                Intent intent1 = new Intent(PhongChatBanBe.this,TaoPhongChat.class);
                intent1.putExtra("maso",maso);
                startActivity(intent1);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.phongmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}