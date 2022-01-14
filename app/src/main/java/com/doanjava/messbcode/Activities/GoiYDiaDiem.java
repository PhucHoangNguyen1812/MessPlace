package com.doanjava.messbcode.Activities;

import static com.doanjava.messbcode.GuiThongBao.GuiThongBao;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.doanjava.messbcode.Adapters.ChuyenDoiDiaDiem;
import com.doanjava.messbcode.Adapters.ChuyenDoiGoiYDiaDiem;
import com.doanjava.messbcode.Adapters.ThemNguoiDung;
import com.doanjava.messbcode.GuiThongBao;
import com.doanjava.messbcode.Models.DiaDiemGoiY;
import com.doanjava.messbcode.Models.DiaDiemYeuThich;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.TinNhan;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityGoiYdiaDiemBinding;
import com.doanjava.messbcode.databinding.ActivityThemBanBeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GoiYDiaDiem extends AppCompatActivity {

    ChuyenDoiGoiYDiaDiem goiYDiaDiem;
    ActivityGoiYdiaDiemBinding binding;
    FirebaseDatabase database;
    ListView listView;
    Intent intent;
    String maso ;
    int sum = 0;
    boolean check = false;
    ArrayList<DiaDiemGoiY> dsDiaDiem = new ArrayList<>();
    ArrayList<DiaDiemGoiY> dsKhongTrung = new ArrayList<>();
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoiYdiaDiemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
        listView = findViewById(R.id.listUser);
        database = FirebaseDatabase.getInstance();
        maso = Setmaso();
        binding.txtDanhSach.setText("Địa Điểm Gợi Ý");
        database.getReference().child("DiaDiemYeuThich").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goiYDiaDiem = new ChuyenDoiGoiYDiaDiem(GoiYDiaDiem.this, R.layout.goiy_diadiem,maso);
                dsDiaDiem.clear();
                goiYDiaDiem.clear();
                dsKhongTrung.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    for(DataSnapshot snapshot2 : snapshot1.getChildren())
                    {
                        DiaDiemGoiY DiaDiemGoiY = snapshot2.getValue(DiaDiemGoiY.class);
                        DiaDiemGoiY.setMaDiaDiem(snapshot2.getKey());
                        dsDiaDiem.add(DiaDiemGoiY);
                    }
                }

                for(DiaDiemGoiY diaDiemGoiY : dsDiaDiem) {
                    check = false;
                    for (DiaDiemGoiY diaDiemGoiY1 : dsKhongTrung) {
                        if(diaDiemGoiY.getLatitude() == diaDiemGoiY1.getLatitude() && diaDiemGoiY.getLongitude() == diaDiemGoiY1.getLongitude()) {
                            check = true;
                        }
                    }
                    if(check == false)
                    {
                        dsKhongTrung.add(diaDiemGoiY);
                    }
                }


                for (DiaDiemGoiY diaDiemGoiY : dsKhongTrung) {
                    sum = 0;
                    for (DiaDiemGoiY diaDiemGoiY1 : dsDiaDiem)
                        if (diaDiemGoiY.getLatitude() == diaDiemGoiY1.getLatitude() && diaDiemGoiY.getLongitude() == diaDiemGoiY1.getLongitude())
                            sum++;

                    diaDiemGoiY.setSum(sum);
                }

                dsKhongTrung.sort((o1, o2) -> o2.getSum() - o1.getSum());

                for(DiaDiemGoiY diaDiemGoiY : dsKhongTrung)
                    goiYDiaDiem.add(diaDiemGoiY);

                listView.setAdapter(goiYDiaDiem);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent = new Intent(GoiYDiaDiem.this, Main_Map.class);
                    Geocoder geocoder = new Geocoder(GoiYDiaDiem.this);
                    ArrayList<Address> addressList = null;
                    try {
                        addressList = (ArrayList<Address>) geocoder.getFromLocation(goiYDiaDiem.getItem(position).getLatitude(),goiYDiaDiem.getItem(position).getLongitude(),1);
                        Address address = addressList.get(0);
                        if(address != null)
                            intent.putExtra("timkiem", address.getAddressLine(0));
                        maso = Setmaso();
                        intent.putExtra("maso",maso);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                    finish();
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String maHoatDong = "NguoiDung " + maso;
        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Trực tuyến");

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nhom:
                maso = Setmaso();
                Intent intent = new Intent(GoiYDiaDiem.this, NhanTinDiaDiem.class);
                intent.putExtra("maNguoiGui",maso);
                startActivity(intent);
                break;
            case R.id.timkiem:
                if(binding.txttimkiem.getVisibility() == View.GONE)
                    binding.txttimkiem.setVisibility(View.VISIBLE);
                else
                    binding.txttimkiem.setVisibility(View.GONE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onPause() {
        super.onPause();
        maso = Setmaso();
        String maHoatDong = "NguoiDung " + maso;
        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Ngoại tuyến");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menuchatdiadiem, menu);
        return super.onCreateOptionsMenu(menu);
    }
}


