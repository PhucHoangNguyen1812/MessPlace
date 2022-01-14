package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Adapters.ChuyenDoiNguoiDung;
import com.doanjava.messbcode.Models.BanBeTaoPhong;
import com.doanjava.messbcode.Models.DiaDiemYeuThich;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.PhongChat;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityMainBinding;
import com.doanjava.messbcode.databinding.ActivityTaoPhongChatBinding;
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
import java.util.Date;
import java.util.HashMap;

public class TaoPhongChat extends AppCompatActivity {
    ActivityTaoPhongChatBinding binding;
    FirebaseDatabase database;
    ArrayList<NguoiDung> nguoiDungs = new ArrayList<>();
    String maso;
    boolean checkthem = false;
    long max;
    private Intent chonAnh;
    NguoiDung nguoiDung = new NguoiDung();
    PhongChat phongChat = new PhongChat();
    ArrayList<BanBeTaoPhong> dskb = new ArrayList<>();
    ProgressDialog dialog;

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

    public void TimKiemList(ArrayAdapter<BanBeTaoPhong> arrayAdapter) {
        String chuoi ;

        chuoi = BoDauCach(binding.txtSearchBb.getText().toString().toUpperCase());
        binding.listDSBB.setAdapter(arrayAdapter);
            ArrayList<BanBeTaoPhong> timkiems = new ArrayList<>();
            for (BanBeTaoPhong nd:dskb) {
                if(BoDauCach(nd.getTen().toUpperCase()).contains(chuoi) || BoDauCach(nd.getMaso().toUpperCase()).contains(chuoi)){
                    timkiems.add(nd);
                }
            }
            ArrayAdapter<BanBeTaoPhong> arrayAdapter1 = new ArrayAdapter<BanBeTaoPhong>(TaoPhongChat.this, android.R.layout.simple_list_item_checked , timkiems);
            for(int i=0;i< timkiems.size(); i++ )  {
                binding.listDSBB.setItemChecked(i,timkiems.get(i).isCheck());
                binding.listDSBB.setAdapter(arrayAdapter1);
        }
    }

    public void TaoPhong (String maso)
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
                            {
                                BanBeTaoPhong kb = new BanBeTaoPhong();
                                kb.setCheck(false);
                                kb.setMaso(ng.getMaSo());
                                kb.setTen(ng.getTen());
                                dskb.add(kb);
                            }

                        }
                    }
                }
                binding.listDSBB.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

                ArrayAdapter<BanBeTaoPhong> arrayAdapter
                        = new ArrayAdapter<BanBeTaoPhong>(TaoPhongChat.this, android.R.layout.simple_list_item_checked , dskb);
                binding.listDSBB.setAdapter(arrayAdapter);
                binding.txtSearchBb.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        TimKiemList(arrayAdapter);
                        return false;
                    }
                });

                for(int i=0;i< dskb.size(); i++ )  {
                    binding.listDSBB.setItemChecked(i,dskb.get(i).isCheck());
                }
                binding.listDSBB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckedTextView v = (CheckedTextView) view;
                        boolean check = v.isChecked();
                        BanBeTaoPhong user = (BanBeTaoPhong) binding.listDSBB.getItemAtPosition(position);
                        user.setCheck(!check);
                        dskb.get(position).setCheck(v.isChecked());
                        String thanhvien = "Thành Viên: ";
                        for(BanBeTaoPhong bb : dskb)
                            if (bb.isCheck() == true)
                                thanhvien += bb.getTen() + ", ";
                        binding.txtThanhVien.setText(thanhvien);

                    }
                });
                binding.btnThemAnh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 25);
                    }
                });

                binding.btnThemNhom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(chonAnh!= null && !binding.txtTenPhongChat.getText().toString().equals("") && !binding.txtThanhVien.getText().equals("")) {
                            try {
                                dialog.show();
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                Date date = new Date();
                                StorageReference reference = storage.getReference()
                                        .child("PhongChat")
                                        .child(date.getTime() + "");
                                reference.putFile(chonAnh.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String linkAnh = uri.toString();
                                                    String tenPhong = binding.txtTenPhongChat.getText().toString();
                                                    ArrayList<String> thanhVien =new ArrayList<>();
                                                    ArrayList<String> quanTri = new ArrayList<>();
                                                    quanTri.add(maso);
                                                    for(BanBeTaoPhong tv : dskb)
                                                    {
                                                        if(tv.isCheck() == true)
                                                            thanhVien.add(tv.getMaso());
                                                    }
                                                    PhongChat phongChat = new PhongChat(tenPhong, linkAnh, maso,quanTri,thanhVien);
                                                    checkthem = true;
                                                    database.getReference().child("PhongChat").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(checkthem == true) {
                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                                                String ngaygio = dateFormat.format(new Date().getTime());
                                                                HashMap<String, Object> tinCuoiObj = new HashMap<>();
                                                                tinCuoiObj.put("tinCuoi",nguoiDung.getTen() + " vừa tạo một nhóm có mặt bạn");
                                                                tinCuoiObj.put("thoiGianTinCuoi",ngaygio);
                                                                max = snapshot.getChildrenCount() + 1;
                                                                database.getReference()
                                                                        .child("PhongChat")
                                                                        .child(max + "")
                                                                        .setValue(phongChat);
                                                                database.getReference().child("PhongChat").child(max + "").updateChildren(tinCuoiObj);
                                                                chonAnh = null;
                                                                checkthem = false;
                                                                dialog.dismiss();
                                                                Toast.makeText(TaoPhongChat.this, "TẠO THÀNH CÔNG", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }catch (Exception ex){
                                Toast.makeText(TaoPhongChat.this,"TẠO THẤT BẠI",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            if(chonAnh == null)
                                Toast.makeText(TaoPhongChat.this,"Vui Lòng Thêm Ảnh Phòng Chat",Toast.LENGTH_SHORT).show();
                            if(binding.txtThanhVien.getText().equals(""))
                            Toast.makeText(TaoPhongChat.this,"Vui Lòng Thêm Thành Viên Phòng Chat",Toast.LENGTH_SHORT).show();
                            if(binding.txtTenPhongChat.getText().toString().equals(""))
                            Toast.makeText(TaoPhongChat.this,"Vui Lòng Đặt Tên Phòng Chat",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaoPhongChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = FirebaseDatabase.getInstance();
        maso = Setmaso();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tạo phòng chat...");
        dialog.setCancelable(false);

        phongChat = (PhongChat) getIntent().getSerializableExtra("PhongChat");


            database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nguoiDungs.clear();
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        NguoiDung ng = snapshot1.getValue(NguoiDung.class);
                        if(ng.getMaSo().equals(maso))
                            nguoiDung = ng;
                        else
                            nguoiDungs.add(ng);
                    }
                    if(phongChat == null) {
                        getSupportActionBar().setTitle("TẠO PHÒNG CHAT");
                        TaoPhong(nguoiDung.getMaSo());
                    }
                    else
                    {
                        nguoiDungs.add(nguoiDung);
                        getSupportActionBar().setTitle("THÔNG TIN PHÒNG CHAT");
                        binding.txtTenPhongChat.setEnabled(false);
                        Glide.with(TaoPhongChat.this)
                                .load(phongChat.getLinkAnh())
                                .into(binding.anhPhongChat);
                        binding.btnThemNhom.setVisibility(View.GONE);
                        binding.btnThemAnh.setVisibility(View.GONE);
                        binding.txtTenPhongChat.setText(phongChat.getTenPhong());
                        dskb.clear();
                        String nguoitao = " - Người Tạo: ";
                        String quantri = " - Quản Trị: ";
                        String ten =" - Thành Viên: ";
                        for(NguoiDung ng : nguoiDungs)
                        {
                            if(ng.getMaSo().equals(maso))
                                ng.setTen("Bạn");
                            BanBeTaoPhong thanhvien = new BanBeTaoPhong();
                            thanhvien.setMaso(ng.getMaSo());
                            thanhvien.setTen(ng.getTen());

                            if(phongChat.getNguoiTao().equals(ng.getMaSo())) {
                                nguoitao += ng.getTen();
                            }
                            if(phongChat.getQuanTri() != null)
                                if(phongChat.getQuanTri().contains(ng.getMaSo())) {
                                    thanhvien.setChucvu(" - Quản Trị");
                                    dskb.add(thanhvien);
                                    quantri += ng.getTen() + ",";
                            }
                            if(phongChat.getThanhVien() != null)
                                if(phongChat.getThanhVien().contains(ng.getMaSo())){
                                    thanhvien.setChucvu(" - Thành Viên");
                                    dskb.add(thanhvien);
                                    ten += ng.getTen() + ",";
                            }
                        }
                        ten = nguoitao + "\n" + quantri +  "\n" + ten;
                        binding.listDSBB.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
                        ArrayAdapter<BanBeTaoPhong> arrayAdapter
                                = new ArrayAdapter<BanBeTaoPhong>(TaoPhongChat.this, android.R.layout.simple_list_item_1 , dskb);
                        binding.listDSBB.setAdapter(arrayAdapter);
                        binding.txtThanhVien.setText(ten);
                        binding.txtSearchBb.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                TimKiemList(arrayAdapter);
                                return false;
                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        String maHoatDong = "NguoiDung " + maso;

        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Trực tuyến");



    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    binding.anhPhongChat.setImageURI(data.getData());
                    chonAnh = data;
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}