package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.doanjava.messbcode.Adapters.ChuyenDoiNguoiDung;
import com.doanjava.messbcode.Adapters.ChuyenDoiTrangThai;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.CauChuyen;
import com.doanjava.messbcode.Models.TrangThaiNguoiDung;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<NguoiDung> nguoiDungs;
    ChuyenDoiNguoiDung chuyenDoiNguoiDung;
    ChuyenDoiTrangThai chuyenDoiTrangThai;
    ArrayList<TrangThaiNguoiDung> trangThaiNguoiDungs;
    ProgressDialog dialog;
    int max;
    String sttTrangThai;
    String maso;
    NguoiDung nguoiDung = new NguoiDung();
    ArrayList<NguoiDung> dskb = new ArrayList<>();

    public String Setmaso()
    {
        maso = getIntent().getStringExtra("maso");
        return maso;
    }

    public int TimMax (String maso)
    {

        database.getReference().child("CauChuyen").child(maso).child("trangThai").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                max = 0;
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    if(Integer.parseInt(snapshot1.getKey()) > max)
                        max = Integer.parseInt(snapshot1.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return max + 1;
    }

    public void LayDSKB (String maso)
    {
        database.getReference().child("TroChuyen").child(maso).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dskb.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getChildrenCount() > 0)
                    {
                        for(NguoiDung ng : nguoiDungs)
                        {
                            if(ng.getMaSo().equals(snapshot1.getKey()))
                                dskb.add(ng);
                        }
                    }
                }
                binding.recyclerView.hideShimmerAdapter();
                chuyenDoiNguoiDung.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        maso = Setmaso();

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("token",token);
                        database.getReference()
                                .child("NguoiDung")
                                .child(maso)
                                .updateChildren(map);

                    }
                });

        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang Tải Hinh Ảnh Lên...");
        dialog.setCancelable(false);

        nguoiDungs = new ArrayList<>();
        trangThaiNguoiDungs = new ArrayList<>();

        chuyenDoiTrangThai = new ChuyenDoiTrangThai(MainActivity.this, trangThaiNguoiDungs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(layoutManager);
        binding.statusList.setAdapter(chuyenDoiTrangThai);

        binding.recyclerView.showShimmerAdapter();
        binding.statusList.showShimmerAdapter();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!= null) {
            if(data.getData() != null) {
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference()
                        .child("trangThai")
                        .child(date.getTime() + "");

                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a dd:MM:yyyy");
                String ngaygio = dateFormat.format(new Date().getTime());
                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    TrangThaiNguoiDung trangThaiNguoiDung = new TrangThaiNguoiDung();
                                    trangThaiNguoiDung.setNguoiDang(nguoiDung.getMaSo());
                                    trangThaiNguoiDung.setAnhDaiDien(nguoiDung.getAnhDaiDien());
                                    trangThaiNguoiDung.setCapNhatCuoi(ngaygio);

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("nguoiDang", trangThaiNguoiDung.getNguoiDang());
                                    obj.put("anhDaiDien", trangThaiNguoiDung.getAnhDaiDien());
                                    obj.put("capNhatCuoi", trangThaiNguoiDung.getCapNhatCuoi());


                                    String linkAnh = uri.toString();
                                    CauChuyen cauChuyen = new CauChuyen(linkAnh, trangThaiNguoiDung.getCapNhatCuoi());
                                    String maso = "NguoiDung: " + Setmaso();

                                    database.getReference()
                                            .child("CauChuyen")
                                            .child(maso)
                                            .updateChildren(obj);

                                    sttTrangThai = "" + TimMax(maso);
                                    database.getReference().child("CauChuyen")
                                            .child(maso)
                                            .child("trangThai")
                                            .child(sttTrangThai)
                                            .setValue(cauChuyen);

                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
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
        chuoi = BoDauCach(binding.txtSearchBanBeChat.getText().toString().toUpperCase());
        if(chuoi == null || chuoi.equals(""))
            chuyenDoiNguoiDung = new ChuyenDoiNguoiDung(this,nguoiDung, dskb);
        else
        {
            ArrayList<NguoiDung> timkiems = new ArrayList<>();
            for (NguoiDung nguoiDung:nguoiDungs) {
                if(BoDauCach(nguoiDung.getTen().toUpperCase()).contains(chuoi) || BoDauCach(nguoiDung.getMaSo().toUpperCase()).contains(chuoi)){
                      timkiems.add(nguoiDung);
                }
            }
            chuyenDoiNguoiDung = new ChuyenDoiNguoiDung(this,nguoiDung, timkiems);
        }
        binding.recyclerView.setAdapter(chuyenDoiNguoiDung);
    }

    public void ChuyenDoiStory(){
        database.getReference().child("CauChuyen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()    ) {
                    trangThaiNguoiDungs.clear();
                    chuyenDoiTrangThai.setNguoiDungs(nguoiDungs);
                    chuyenDoiTrangThai.setNguoiDung(nguoiDung);
                    for(DataSnapshot storySnapShot : snapshot.getChildren()) {
                        TrangThaiNguoiDung trangThai = new TrangThaiNguoiDung();
                        trangThai.setNguoiDang(storySnapShot.child("nguoiDang").getValue(String.class));
                        trangThai.setAnhDaiDien(storySnapShot.child("anhDaiDien").getValue(String.class));
                        trangThai.setCapNhatCuoi(storySnapShot.child("capNhatCuoi").getValue(String.class));
                        ArrayList<CauChuyen> cauChuyens = new ArrayList<>();
                        if(trangThai.getNguoiDang().equals(maso)) {
                            for (DataSnapshot statusSnapshot : storySnapShot.child("trangThai").getChildren()) {
                                CauChuyen sampleStatus = statusSnapshot.getValue(CauChuyen.class);
                                cauChuyens.add(sampleStatus);
                            }
                            trangThai.setCauChuyens(cauChuyens);
                            trangThaiNguoiDungs.add(trangThai);
                        }
                        else{
                            for(NguoiDung ng : dskb)
                            {
                                if(trangThai.getNguoiDang().equals(ng.getMaSo()))
                                {
                                    for (DataSnapshot statusSnapshot : storySnapShot.child("trangThai").getChildren()) {
                                        CauChuyen sampleStatus = statusSnapshot.getValue(CauChuyen.class);
                                        cauChuyens.add(sampleStatus);
                                    }
                                    trangThai.setCauChuyens(cauChuyens);
                                    trangThaiNguoiDungs.add(trangThai);
                                }
                            }

                        }
                    }
                    binding.statusList.hideShimmerAdapter();
                    chuyenDoiTrangThai.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.txtSearchBanBeChat.setVisibility(View.GONE);

        maso = Setmaso();
        binding.txtSearchBanBeChat.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                TimKiemList();
                return false;
            }
        });


        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.status:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 75);
                        break;
                    case  R.id.map_chinh:
                        Intent intent0 = new Intent(MainActivity.this, Main_Map.class);
                        intent0.putExtra("maso", maso);
                        startActivity(intent0);
                        break;
                }
                return false;
            }
        });

        maso = Setmaso();
        database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nguoiDungs.clear();
                maso = Setmaso();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    NguoiDung ng = snapshot1.getValue(NguoiDung.class);
                    if(ng.getMaSo().equals(maso))
                        nguoiDung = ng;
                    else
                        nguoiDungs.add(ng);
                }
                LayDSKB(maso);
                ChuyenDoiStory();
                chuyenDoiNguoiDung = new ChuyenDoiNguoiDung(MainActivity.this,nguoiDung, dskb);
                binding.recyclerView.setAdapter(chuyenDoiNguoiDung);
                chuyenDoiNguoiDung.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String maHoatDong = "NguoiDung " + maso;
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
                Intent intent = new Intent(MainActivity.this, NhanTinDiaDiem.class);
                intent.putExtra("maNguoiGui",maso);
                startActivity(intent);
                break;
            case R.id.timkiem:
                if(binding.txtSearchBanBeChat.getVisibility() == View.GONE)
                    binding.txtSearchBanBeChat.setVisibility(View.VISIBLE);
                else
                    binding.txtSearchBanBeChat.setVisibility(View.GONE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}