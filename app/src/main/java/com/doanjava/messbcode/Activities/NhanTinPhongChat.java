package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.doanjava.messbcode.Adapters.ChuyenDoiTinNhanDiaDiem;
import com.doanjava.messbcode.Adapters.ChuyenDoiTinNhanPhong;
import com.doanjava.messbcode.GuiThongBao;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.PhongChat;
import com.doanjava.messbcode.Models.TinNhan;
import com.doanjava.messbcode.Models.ViTriGPS;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityNhanTinPhongChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class NhanTinPhongChat extends AppCompatActivity {

    ActivityNhanTinPhongChatBinding binding;
    ChuyenDoiTinNhanPhong chuyenDoiTinNhanPhong;
    ArrayList<TinNhan> tinNhans;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String maSoNguoiGui;
    PhongChat phongChat;
    ArrayList<NguoiDung> nguoiDungs = new ArrayList<>();
    int max;
    String sttTin;
    String tennggui = "";
    boolean checkguivitri = false;

    public int TimMax ()
    {

        database.getReference().child("PhongChat").child(phongChat.getMaPhong()).child("tinNhan").addValueEventListener(new ValueEventListener() {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityNhanTinPhongChatBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        maSoNguoiGui = getIntent().getStringExtra("maNguoiGui");
        phongChat = (PhongChat) getIntent().getSerializableExtra("PhongChat");



    }

    @Override
    protected void onResume() {
        super.onResume();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        String token = getIntent().getStringExtra("token");
        String ten = getIntent().getStringExtra("ten");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        database.getReference().child("PhongChat")
                .child(phongChat.getMaPhong()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean check = false;
                 for(DataSnapshot snapshot1 : snapshot.getChildren())
                 {
                     if(snapshot1.getKey().equals("quanTri"))
                     {
                         for(DataSnapshot snapshot2 : snapshot1.getChildren())
                         {
                             if(snapshot2.getValue().equals(maSoNguoiGui))
                                 check = true;
                         }
                     }
                     if(snapshot1.getKey().equals("thanhVien"))
                     {
                         for(DataSnapshot snapshot2 : snapshot1.getChildren())
                         {
                             if(snapshot2.getValue().equals(maSoNguoiGui))
                                 check = true;
                         }
                     }
                 }
                 if(!check)
                     finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Lấy DS Phòng Chat
        database.getReference().child("PhongChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if(snapshot1.getKey().equals(phongChat.getMaPhong())) {
                        phongChat = snapshot1.getValue(PhongChat.class);
                        phongChat.setMaPhong(snapshot1.getKey());
                        getSupportActionBar().setTitle("Phòng: " + phongChat.getTenPhong().toUpperCase());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tinNhans = new ArrayList<>();
        chuyenDoiTinNhanPhong = new ChuyenDoiTinNhanPhong(this,maSoNguoiGui,phongChat, tinNhans);
        binding.recyclerViewPhong.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewPhong.setAdapter(chuyenDoiTinNhanPhong);

        database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nguoiDungs.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    NguoiDung ng = snapshot1.getValue(NguoiDung.class);
                    nguoiDungs.add(ng);
                }
                chuyenDoiTinNhanPhong.setNguoiDungs(nguoiDungs);
                database.getReference().child("PhongChat").child(phongChat.getMaPhong()).child("tinNhan")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tinNhans.clear();
                                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    TinNhan tinNhan =  snapshot1.getValue(TinNhan.class);
                                    tinNhan.setMaTin(snapshot1.getKey());
                                    tinNhans.add(tinNhan);
                                }

                                chuyenDoiTinNhanPhong.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                sttTin = "" + TimMax();
                binding.sentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tinTxt = binding.messageBox.getText().toString();
                        if(!tinTxt.isEmpty()){

                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                            String ngaygio = dateFormat.format(new Date().getTime());
                            sttTin = "" + TimMax();
                            TinNhan tinNhan = new TinNhan(tinTxt, maSoNguoiGui, ngaygio);
                            binding.messageBox.setText("");
                            HashMap<String, Object> tinCuoiObj = new HashMap<>();
                            tinCuoiObj.put("tinCuoi",tinNhan.getTinNhan());
                            tinCuoiObj.put("thoiGianTinCuoi", ngaygio);

                            database.getReference().child("PhongChat").child(phongChat.getMaPhong()).updateChildren(tinCuoiObj);

                            database.getReference().child("PhongChat")
                                    .child(phongChat.getMaPhong())
                                    .child("tinNhan")
                                    .child(sttTin)
                                    .setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    for(NguoiDung ng : nguoiDungs)
                                        if(ng.getMaSo().equals(maSoNguoiGui))
                                                 tennggui = ng.getTen();

                                     for(NguoiDung ng : nguoiDungs)
                                     {
                                         if(!ng.getMaSo().equals(maSoNguoiGui))
                                         {
                                             if(phongChat.getQuanTri() != null)
                                                 if(phongChat.getQuanTri().contains(ng.getMaSo()))
                                                        GuiThongBao.GuiThongBao(tennggui,tinNhan.getTinNhan(),ng.getToken(),NhanTinPhongChat.this);

                                             if(phongChat.getThanhVien() != null)
                                                 if(phongChat.getThanhVien().contains(ng.getMaSo()))
                                                     GuiThongBao.GuiThongBao(tennggui,tinNhan.getTinNhan(),ng.getToken(),NhanTinPhongChat.this);
                                         }
                                     }
                                }
                            });
                        }
                    }
                });

                binding.attachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent,25);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        String maHoatDong = "NguoiDung " + maSoNguoiGui;
        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Trực tuyến");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String maHoatDong = "NguoiDung " + maSoNguoiGui;
        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Ngoại tuyến");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 25) {
            if(data != null) {
                if(data.getData() != null) {
                    Uri chonAnh = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference()
                            .child("PhongChat")
                            .child(calendar.getTimeInMillis() + "");
                    reference.putFile(chonAnh).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String linkAnh = uri.toString();
                                        sttTin = "" + TimMax();
                                        String tinTxt = binding.messageBox.getText().toString();
                                        String token1 = getIntent().getStringExtra("token");
                                        String ten1 = getIntent().getStringExtra("ten");
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String ngaygio = dateFormat.format(new Date().getTime());
                                        TinNhan tinNhan = new TinNhan(tinTxt, maSoNguoiGui, ngaygio);
                                        tinNhan.setTinNhan("HinhAnh");
                                        tinNhan.setLinkAnhGui(linkAnh);
                                        binding.messageBox.setText("");

                                        database.getReference().child("PhongChat")
                                                .child(phongChat.getMaPhong())
                                                .child("tinNhan")
                                                .child(sttTin)
                                                .setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                GuiThongBao.GuiThongBao(ten1," Đã Gửi Một Hình Ảnh",token1,NhanTinPhongChat.this);
                                            }
                                        });

                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Lấy DS Phòng Chat
        maSoNguoiGui = getIntent().getStringExtra("maNguoiGui");
        phongChat = (PhongChat) getIntent().getSerializableExtra("PhongChat");
        database.getReference().child("PhongChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menu.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if(snapshot1.getKey().equals(phongChat.getMaPhong())) {
                        phongChat = snapshot1.getValue(PhongChat.class);
                        phongChat.setMaPhong(snapshot1.getKey());
                        if(phongChat.getQuanTri() != null)
                        if(phongChat.getQuanTri().contains(maSoNguoiGui))
                            getMenuInflater().inflate(R.menu.menu_quantri, menu);
                        getMenuInflater().inflate(R.menu.menu_thanhvien, menu);
                        if(phongChat.getNguoiTao().equals(maSoNguoiGui))
                            getMenuInflater().inflate(R.menu.menu_nguoitao, menu);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.xoaphong:
                database.getReference().child("PhongChat")
                        .child(phongChat.getMaPhong())
                        .setValue(null);
                Toast.makeText(NhanTinPhongChat.this,"PHÒNG ĐÃ XÓA" ,Toast.LENGTH_SHORT).show();
                finish();
                break;

            case R.id.roinhom:
                ArrayList<String> roiPhong = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String ngaygio = dateFormat.format(new Date().getTime());
                HashMap<String, Object> tinCuoiObj = new HashMap<>();



                if(phongChat.getQuanTri()!=null)
                    if(phongChat.getQuanTri().contains(maSoNguoiGui))
                    {
                        for(String tv : phongChat.getQuanTri())
                            if(!tv.equals(maSoNguoiGui))
                                roiPhong.add(tv);
                        database.getReference().child("PhongChat")
                                .child(phongChat.getMaPhong())
                                .child("quanTri").setValue(roiPhong);

                    }
                if(phongChat.getThanhVien()!=null)
                    if(phongChat.getThanhVien().contains(maSoNguoiGui))
                    {
                        for(String tv : phongChat.getThanhVien())
                            if(!tv.equals(maSoNguoiGui))
                                roiPhong.add(tv);
                        database.getReference().child("PhongChat")
                                .child(phongChat.getMaPhong())
                                .child("thanhVien").setValue(roiPhong);
                    }


                for(NguoiDung ng : nguoiDungs)
                {
                    if(ng.getMaSo().equals(maSoNguoiGui))
                    {
                        tinCuoiObj.put("tinCuoi",ng.getTen() + " vừa rời khỏi nhóm..");
                        tinCuoiObj.put("thoiGianTinCuoi",ngaygio);
                        database.getReference().child("PhongChat").child(phongChat.getMaPhong()).updateChildren(tinCuoiObj);
                        Toast.makeText(NhanTinPhongChat.this,"Đã rời phòng chat: " + phongChat.getTenPhong().toUpperCase(),Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(NhanTinPhongChat.this,"Bạn đã rời phòng " + phongChat.getTenPhong().toUpperCase(),Toast.LENGTH_SHORT).show();





                int dem = 0;
                if(phongChat.getQuanTri() != null)
                    dem = phongChat.getQuanTri().size();
                if(phongChat.getThanhVien() != null)
                    dem += phongChat.getThanhVien().size();
                if(dem <= 1)
                {
                    database.getReference().child("PhongChat")
                            .child(phongChat.getMaPhong())
                            .setValue(null);
                }
                else
                {

                    if(phongChat.getQuanTri() != null && phongChat.getThanhVien() != null)
                            if(phongChat.getThanhVien().size() > 0 && phongChat.getQuanTri().size() == 1)
                            {
                                    phongChat.setQuanTri(phongChat.getThanhVien());
                                    phongChat.setThanhVien(null);
                                    HashMap<String, Object> capnhap = new HashMap<>();
                                    capnhap.put("thanhVien",phongChat.getThanhVien());
                                    capnhap.put("quanTri",phongChat.getQuanTri());
                                    capnhap.put("tinCuoi","Các thành viên còn lại sẽ trở thành quản trị");
                                    capnhap.put("thoiGianTinCuoi",ngaygio);
                                    database.getReference()
                                        .child("PhongChat")
                                        .child(phongChat.getMaPhong())
                                        .updateChildren(capnhap);

                            }
               }




                finish();
                break;
            case R.id.thongtinPhong:
                Intent intent = new Intent(this, TaoPhongChat.class);
                intent.putExtra("maso",maSoNguoiGui);
                intent.putExtra("PhongChat",phongChat);
                startActivity(intent);
                break;
            case R.id.quanlyphong:
                Intent intent1 = new Intent(this, QuanLyPhongChat.class);
                intent1.putExtra("maso",maSoNguoiGui);
                intent1.putExtra("PhongChat",phongChat);
                startActivity(intent1);
                break;
            case R.id.guivitri:
                checkguivitri = true;
                database.getReference().child("ViTri").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for(DataSnapshot snapshot1 : snapshot.getChildren())
                            {
                                if(snapshot1.getKey().equals(maSoNguoiGui))
                                {
                                    ViTriGPS vitri = snapshot1.getValue(ViTriGPS.class);
                                    try{
                                        ArrayList<Address> addressList = new ArrayList<>();
                                        Geocoder geocoder = new Geocoder(NhanTinPhongChat.this);
                                        addressList = (ArrayList<Address>) geocoder.getFromLocation(vitri.getLatitude(),vitri.getLongitude(),1);
                                        Address address = addressList.get(0);
                                        if(address != null) {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                            String ngaygio = dateFormat.format(new Date().getTime());
                                            String[] chuoidiadiem = address.getAddressLine(0).split(",");
                                            String chuoi = "Hiện đang ở tại: ";
                                            for(String a : chuoidiadiem)
                                                chuoi += "\n" + a ;
                                            TinNhan tinNhan = new TinNhan( chuoi, maSoNguoiGui, ngaygio);
                                            sttTin = "" + TimMax();

                                            if (checkguivitri == true) {
                                                checkguivitri = false;
                                                sttTin = "" + TimMax();
                                                database.getReference().child("PhongChat")
                                                        .child(phongChat.getMaPhong())
                                                        .child("tinNhan")
                                                        .child(sttTin)
                                                        .setValue(tinNhan);
                                            }
                                        }
                                    }catch (Exception e) {
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}