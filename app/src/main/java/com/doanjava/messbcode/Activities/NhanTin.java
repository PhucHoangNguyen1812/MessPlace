package com.doanjava.messbcode.Activities;

import static com.doanjava.messbcode.GuiThongBao.GuiThongBao;

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
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Adapters.ChuyenDoiTinNhan;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.TinNhan;
import com.doanjava.messbcode.Models.ViTriGPS;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityNhanTinBinding;
import com.doanjava.messbcode.listener.UserListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.List;

public class NhanTin extends AppCompatActivity  {


    ActivityNhanTinBinding binding;
    ChuyenDoiTinNhan chuyenDoiTinNhan;
    ArrayList<TinNhan> tinNhans;

    String maSoNguoiGui;
    String maSoNguoiNhan;
    NguoiDung NguoiGui = new NguoiDung();
    NguoiDung NguoiNhan = new NguoiDung();

    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog dialog;
    int maxnn;
    int maxng;
    String sosanh;
    String token;
    boolean checkguivitri = false;

    UserListener userListener = new UserListener() {
        @Override
        public void initiateVideoMeeting(NguoiDung nguoiNhan) {
            //Toast.makeText(NhanTin.this,nguoiNhan.getTen() + "-" + nguoiNhan.getToken() + "",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NhanTin.this,Off_Video.class);
            intent.putExtra("nguoinhan",nguoiNhan);
            intent.putExtra("type","video");
            intent.putExtra("nguoigui",NguoiGui);
            startActivity(intent);
        }

        @Override
        public void initiateAudioMeeting(NguoiDung nguoiNhan) {
           // Toast.makeText(NhanTin.this,nguoiNhan.getTen() + "-" + nguoiNhan.getToken() + "",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NhanTin.this,Off_Video.class);
            intent.putExtra("nguoinhan",nguoiNhan);
            intent.putExtra("nguoigui",NguoiGui);
            startActivity(intent);

        }
    };



    public int TimMaxnn (String trochuyen)
    {

        database.getReference().child("TroChuyen").child(maSoNguoiNhan).child(trochuyen).child("tinNhan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    maxnn = 0;
                    for(DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        if(Integer.parseInt(snapshot1.getKey()) > maxnn)
                            maxnn = Integer.parseInt(snapshot1.getKey());
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return maxnn + 1;
    }

    public int TimMaxng (String trochuyen)
    {
        database.getReference().child("TroChuyen").child(maSoNguoiGui).child(trochuyen).child("tinNhan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                maxng = 0;
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    if(Integer.parseInt(snapshot1.getKey()) > maxng)
                        maxng = Integer.parseInt(snapshot1.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return maxng + 1;
    }

    public String SoSanh (int maxNguoiGui,int maxNguoiNhan)
    {
        if(maxNguoiGui >= maxNguoiNhan)
            return maxNguoiGui + "";
        else
            return maxNguoiNhan + "";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNhanTinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog( this);
        dialog.setMessage("Đang Tải Ảnh...");
        dialog.setCancelable(false);

        tinNhans = new ArrayList<>();

        String token = getIntent().getStringExtra("token");
        String ten = getIntent().getStringExtra("ten");
        String anhDaiDien = getIntent().getStringExtra("hinh");

        NguoiNhan = (NguoiDung) getIntent().getSerializableExtra("NguoiNhan");
        NguoiGui = (NguoiDung) getIntent().getSerializableExtra("NguoiGui");
        maSoNguoiNhan = NguoiNhan.getMaSo();
        maSoNguoiGui = NguoiGui.getMaSo();

        binding.name.setText(NguoiNhan.getTen());
        Glide.with(NhanTin.this).load(NguoiNhan.getAnhDaiDien())
                .placeholder(R.drawable.avatar)
                .into(binding.profile);

        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        database.getReference().child("TroChuyen")
                .child(maSoNguoiGui)
                .child(maSoNguoiNhan).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        database.getReference().child("TrangThaiHoatDong").child("NguoiDung " + maSoNguoiNhan).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String trangThai = snapshot.getValue(String.class);
                    if(!trangThai.isEmpty()) {
                        if(trangThai.equals("Ngoại tuyến")) {
                            binding.status.setVisibility(View.GONE);
                        } else {
                            binding.status.setText(trangThai);
                            binding.status.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chuyenDoiTinNhan = new ChuyenDoiTinNhan(this, tinNhans,NguoiGui,NguoiNhan,NguoiNhan.getTen(),token);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(chuyenDoiTinNhan);

        database.getReference().child("TroChuyen")
                .child(maSoNguoiGui)
                .child(maSoNguoiNhan)
                .child("tinNhan")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tinNhans.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                            TinNhan tinNhan =  snapshot1.getValue(TinNhan.class);
                            tinNhan.setMaTin(snapshot1.getKey());
                            tinNhans.add(tinNhan);
                        }

                        chuyenDoiTinNhan.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Đếm tin nhắn
        sosanh = SoSanh(TimMaxng(maSoNguoiNhan),TimMaxnn(maSoNguoiGui));

        binding.sentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tinTxt = binding.messageBox.getText().toString();

                if(!tinTxt.isEmpty())
                {
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String ngaygio = dateFormat.format(new Date().getTime());
                TinNhan tinNhan = new TinNhan(tinTxt, maSoNguoiGui,ngaygio);
                binding.messageBox.setText("");

                sosanh = SoSanh(TimMaxng(maSoNguoiNhan),TimMaxnn(maSoNguoiGui));

                HashMap<String, Object> tinCuoiObj = new HashMap<>();
                tinCuoiObj.put("tinCuoi",tinNhan.getTinNhan());
                tinCuoiObj.put("thoiGianTinCuoi",ngaygio);

                database.getReference().child("TroChuyen").child(maSoNguoiGui).child(maSoNguoiNhan).updateChildren(tinCuoiObj);
                database.getReference().child("TroChuyen").child(maSoNguoiNhan).child(maSoNguoiGui).updateChildren(tinCuoiObj);

                database.getReference().child("TroChuyen")
                        .child(maSoNguoiGui)
                        .child(maSoNguoiNhan)
                        .child("tinNhan")
                        .child(sosanh)
                        .setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("TroChuyen")
                                .child(maSoNguoiNhan)
                                .child(maSoNguoiGui)
                                .child("tinNhan")
                                .child(sosanh)
                                .setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                GuiThongBao(ten,tinNhan.getTinNhan(),token,NhanTin.this);
                            }
                        });
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

        final Handler handler = new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("TrangThaiHoatDong").child("NguoiDung " + maSoNguoiGui).setValue("Đang gõ...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(StopTyping,1000);
            }

            Runnable StopTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("TrangThaiHoatDong").child("NguoiDung " + maSoNguoiGui).setValue("Trực tuyến");
                }
            };
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);
       // getSupportActionBar().setTitle(ten);

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                            .child("TroChuyen")
                            .child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(chonAnh).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String linkAnh = uri.toString();
                                        String ten1 = getIntent().getStringExtra("ten");
                                        String token1 = getIntent().getStringExtra("token");
                                        String tinTxt = binding.messageBox.getText().toString();

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String ngaygio = dateFormat.format(new Date().getTime());
                                        TinNhan tinNhan = new TinNhan(tinTxt, maSoNguoiGui,ngaygio);
                                        tinNhan.setTinNhan("HinhAnh");
                                        tinNhan.setLinkAnhGui(linkAnh);
                                        binding.messageBox.setText("");

                                        sosanh = SoSanh(TimMaxng(maSoNguoiNhan),TimMaxnn(maSoNguoiGui));
                                        HashMap<String, Object> tinCuoiObj = new HashMap<>();
                                        tinCuoiObj.put("tinCuoi",tinNhan.getTinNhan());
                                        tinCuoiObj.put("thoiGianTinCuoi", ngaygio);

                                        database.getReference().child("TroChuyen").child(maSoNguoiGui).child(maSoNguoiNhan).updateChildren(tinCuoiObj);
                                        database.getReference().child("TroChuyen").child(maSoNguoiNhan).child(maSoNguoiGui).updateChildren(tinCuoiObj);

                                        database.getReference().child("TroChuyen")
                                                .child(maSoNguoiGui)
                                                .child(maSoNguoiNhan)
                                                .child("tinNhan")
                                                .child(sosanh)
                                                .setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                database.getReference().child("TroChuyen")
                                                        .child(maSoNguoiNhan)
                                                        .child(maSoNguoiGui)
                                                        .child("tinNhan")
                                                        .child(sosanh)
                                                        .setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        GuiThongBao(ten1,ten1 + " đã gửi 1 ảnh",token1,NhanTin.this);
                                                    }
                                                });
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
    protected void onResume() {
        super.onResume();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_call:
                String number = NguoiNhan.getSoDienThoai();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" +number));
                startActivity(intent);
                break;
                //userListener.initiateAudioMeeting(NguoiNhan);

            case R.id.ic_videocall:
                userListener.initiateVideoMeeting(NguoiNhan);

                break;
            case R.id.thongtin:
                Intent intent0 = new Intent(NhanTin.this,ThongTinNguoiDung.class);
                intent0.putExtra("masonguoixem",maSoNguoiGui);
                intent0.putExtra("maso",maSoNguoiNhan );
                startActivity(intent0);
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
                                        Geocoder geocoder = new Geocoder(NhanTin.this);
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
                                            sosanh = SoSanh(TimMaxng(maSoNguoiNhan), TimMaxnn(maSoNguoiGui));
                                            if (checkguivitri == true) {
                                                checkguivitri = false;
                                                database.getReference().child("TroChuyen")
                                                        .child(maSoNguoiGui)
                                                        .child(maSoNguoiNhan)
                                                        .child("tinNhan")
                                                        .child(sosanh)
                                                        .setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        database.getReference().child("TroChuyen")
                                                                .child(maSoNguoiNhan)
                                                                .child(maSoNguoiGui)
                                                                .child("tinNhan")
                                                                .child(sosanh)
                                                                .setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                            }
                                                        });
                                                    }
                                                });
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
            case R.id.huykb:
                FirebaseDatabase.getInstance().getReference()
                        .child("TroChuyen")
                        .child(maSoNguoiGui)
                        .child(maSoNguoiNhan)
                        .setValue(null);
                FirebaseDatabase.getInstance().getReference()
                        .child("TroChuyen")
                        .child(maSoNguoiNhan)
                        .child(maSoNguoiGui)
                        .setValue(null);
                Toast.makeText(NhanTin.this, "Hủy Kết Bạn Thành Công.", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}