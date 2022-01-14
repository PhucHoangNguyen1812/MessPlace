package com.doanjava.messbcode.Activities;

import static com.doanjava.messbcode.GuiThongBao.GuiThongBao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.doanjava.messbcode.Adapters.ThemNguoiDung;
import com.doanjava.messbcode.GuiThongBao;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.TinNhan;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityThemBanBeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ThemBanBe extends AppCompatActivity {

    ThemNguoiDung themNguoiDung;
    ActivityThemBanBeBinding binding;
    FirebaseDatabase database;
    ArrayList<NguoiDung> nguoiDungs = new ArrayList<>();
    ProgressDialog dialog;
    ListView listView;
    String maso ;
    ArrayList<String> dskb = new ArrayList<>();
    ArrayList<String> dscho = new ArrayList<>();
    ArrayList<String> dschapnhanYC = new ArrayList<>();
    NguoiDung nguoiDung = new NguoiDung();
    boolean check = false;
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

    public void LayDS(String maso)
    {

        database.getReference().child("TroChuyen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dscho.clear();
                dskb.clear();
                dschapnhanYC.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {

                    if (snapshot1.getKey().equals(maso)) {
                        for (DataSnapshot snapshot2 : snapshot1.getChildren())
                        {
                            if(snapshot2.getChildrenCount()>0)
                                dskb.add(snapshot2.getKey());

                            if (snapshot2.getChildrenCount() == 0)
                                dschapnhanYC.add(snapshot2.getKey());
                        }

                    }
                    for (DataSnapshot snapshot2 : snapshot1.getChildren())
                    {
                        if(snapshot2.getKey().equals(maso)) {
                            dscho.add(snapshot1.getKey());
                        }
                    }
                }
                listView.setAdapter(themNguoiDung);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    GuiNhanYeuCau(themNguoiDung.getItem(position));
                });
                binding.txttimkiem.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        TimKiemList(dskb,dscho,dschapnhanYC);
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void TimKiemList(ArrayList<String> dskb, ArrayList<String> dsCho, ArrayList<String> dschapnhanYC) {
        String chuoi;
        chuoi = BoDauCach(binding.txttimkiem.getText().toString().toUpperCase());
        listView.setAdapter(themNguoiDung);
        ThemNguoiDung listimkiem= new ThemNguoiDung(ThemBanBe.this, R.layout.thongtin_user,maso,0,dskb,dsCho,dschapnhanYC);
        for (int i = 0; i < listView.getCount(); i++) {
            NguoiDung nguoiDung = ((NguoiDung) listView.getItemAtPosition(i));
            if (BoDauCach(nguoiDung.getMaSo().toUpperCase()).contains(chuoi) || BoDauCach(nguoiDung.getTen().toUpperCase()).contains(chuoi) || BoDauCach(nguoiDung.getGioiTinh().toUpperCase()).contains(chuoi))
                listimkiem.add(nguoiDung);
        }
        listView.setAdapter(listimkiem);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            GuiNhanYeuCau(listimkiem.getItem(position));
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemBanBeBinding.inflate(getLayoutInflater());
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

        database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                themNguoiDung = new ThemNguoiDung(ThemBanBe.this, R.layout.thongtin_user,maso,0,dskb,dscho,dschapnhanYC);
                nguoiDungs.clear();
                themNguoiDung.clear();
                maso = Setmaso();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    NguoiDung ng = snapshot1.getValue(NguoiDung.class);
                    if(ng.getMaSo().equals(maso))
                        nguoiDung = ng;
                    else {
                        nguoiDungs.add(ng);
                        themNguoiDung.add(ng);
                    }
                }
                LayDS(maso);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String maHoatDong = "NguoiDung " + maso;
        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Trực tuyến");

    }

    public void GuiNhanYeuCau(NguoiDung nguoiNhan)
    {
        if (dskb.contains(nguoiNhan.getMaSo()))
            Toast.makeText(ThemBanBe.this, "Người Này Đã Là bạn !", Toast.LENGTH_SHORT).show();
        else if(dscho.contains(nguoiNhan.getMaSo()))
        {
             Toast.makeText(ThemBanBe.this, "VUI LÒNG ĐỢI CHẤP NHẬN !", Toast.LENGTH_SHORT).show();
        }
        else if(dschapnhanYC.contains(nguoiNhan.getMaSo()))
        {
            String tinTxt = "Bây giờ các bạn đã là bạn của nhau.Hãy trò chuyện nào!";
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            String ngaygio = dateFormat.format(new Date().getTime());
            TinNhan tinNhan = new TinNhan(tinTxt, nguoiDung.getMaSo(), ngaygio);

            HashMap<String, Object> tinCuoiObj = new HashMap<>();
            tinCuoiObj.put("tinCuoi", tinNhan.getTinNhan());
            tinCuoiObj.put("thoiGianTinCuoi", ngaygio);

            database.getReference().child("TroChuyen").child(nguoiDung.getMaSo()).child(nguoiNhan.getMaSo()).updateChildren(tinCuoiObj);
            database.getReference().child("TroChuyen").child(nguoiNhan.getMaSo()).child(nguoiDung.getMaSo()).updateChildren(tinCuoiObj);

            GuiThongBao.GuiThongBao(nguoiDung.getTen(),nguoiDung.getTen() + " Đã Chấp Nhận Lời Kết Bạn",nguoiNhan.getToken(),ThemBanBe.this);
        }
        else
        {
            database.getReference().child("TroChuyen")
                    .child(nguoiNhan.getMaSo())
                    .child(nguoiDung.getMaSo())
                    .setValue("Đang Chờ Chấp Nhận").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    GuiThongBao(nguoiDung.getTen(),nguoiDung.getTen() + " Đã Gửi Lời Mời Kết Bạn",nguoiNhan.getToken(),ThemBanBe.this);
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nhom:
                maso = Setmaso();
                Intent intent = new Intent(ThemBanBe.this, NhanTinDiaDiem.class);
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


