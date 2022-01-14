package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.doanjava.messbcode.Adapters.ChuyenDoiDiaDiem;
import com.doanjava.messbcode.Models.DiaDiemYeuThich;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityDiaDiemChatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiaDiemChat extends AppCompatActivity {

    ActivityDiaDiemChatBinding binding;
    FirebaseDatabase database;
    ArrayList<DiaDiemYeuThich> diaDiemYeuThichs = new ArrayList<>();
    ChuyenDoiDiaDiem chuyenDoiDiaDiem;
    ProgressDialog dialog;
    ArrayList<Address> addressList = new ArrayList<>();
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
        chuoi = BoDauCach(binding.txtSearchDiaDiemChat.getText().toString().toUpperCase());
        if(chuoi == null || chuoi.equals(""))
            chuyenDoiDiaDiem = new ChuyenDoiDiaDiem(DiaDiemChat.this,maso, diaDiemYeuThichs);
        else
        {
            ArrayList<DiaDiemYeuThich> timkiems = new ArrayList<>();
            for (DiaDiemYeuThich diadiem:diaDiemYeuThichs) {
                addressList.clear();
                Geocoder geocoder = new Geocoder(DiaDiemChat.this);
                try {
                    addressList = (ArrayList<Address>) geocoder.getFromLocation(diadiem.getLatitude(), diadiem.getLongitude(), 1);
                    Address address = addressList.get(0);
                    if (address != null)
                        if (BoDauCach(diadiem.getTenDiaDiem().toUpperCase()).contains(chuoi) || BoDauCach(address.getAddressLine(0).toUpperCase()).contains(chuoi))
                            timkiems.add(diadiem);
                }catch (Exception ex){}
            }
            chuyenDoiDiaDiem = new ChuyenDoiDiaDiem(DiaDiemChat.this,maso, timkiems);
        }
        binding.recyclerView.setAdapter(chuyenDoiDiaDiem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiaDiemChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang Tải Hinh Ảnh Lên...");
        dialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();
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
        binding.txtSearchDiaDiemChat.setVisibility(View.GONE);
        maso = Setmaso();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        chuyenDoiDiaDiem = new ChuyenDoiDiaDiem(DiaDiemChat.this,maso, diaDiemYeuThichs);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(DiaDiemChat.this));
        binding.recyclerView.setAdapter(chuyenDoiDiaDiem);
        binding.recyclerView.showShimmerAdapter();
        String maHoatDong = "NguoiDung " + maso;


        binding.txtSearchDiaDiemChat.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                TimKiemList();
                return false;
            }
        });
        //Lấy DS Địa Điểm
        database.getReference().child("DiaDiemYeuThich").child("NguoiDung: " + maso).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                diaDiemYeuThichs.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    DiaDiemYeuThich diaDiemYeuThich = snapshot1.getValue(DiaDiemYeuThich.class);
                    diaDiemYeuThich.setMaDiaDiem(snapshot1.getKey());
                    diaDiemYeuThichs.add(diaDiemYeuThich);
                }
                binding.recyclerView.hideShimmerAdapter();
                chuyenDoiDiaDiem.notifyDataSetChanged();
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
                Intent intent = new Intent(DiaDiemChat.this, NhanTinDiaDiem.class);
                intent.putExtra("maNguoiGui",maso);
                startActivity(intent);
                break;
            case R.id.timkiem:
                if(binding.txtSearchDiaDiemChat.getVisibility() == View.GONE)
                    binding.txtSearchDiaDiemChat.setVisibility(View.VISIBLE);
                else
                    binding.txtSearchDiaDiemChat.setVisibility(View.GONE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menuchatdiadiem, menu);
        return super.onCreateOptionsMenu(menu);
    }
}