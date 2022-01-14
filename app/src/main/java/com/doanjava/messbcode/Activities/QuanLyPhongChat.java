package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
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
import com.doanjava.messbcode.Models.TinNhan;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityMainBinding;
import com.doanjava.messbcode.databinding.ActivityQuanLyPhongChatBinding;
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

public class QuanLyPhongChat extends AppCompatActivity {
    ActivityQuanLyPhongChatBinding binding;
    FirebaseDatabase database;
    ArrayList<NguoiDung> nguoiDungs = new ArrayList<>();
    String maso;
    boolean checkthem = false;
    String sttTin;
    private Intent chonAnh;
    NguoiDung nguoiDung = new NguoiDung();
    PhongChat phongChat = new PhongChat();
    ArrayList<BanBeTaoPhong> dskb = new ArrayList<>();
    ArrayList<BanBeTaoPhong> dstv = new ArrayList<>();
    ArrayList<BanBeTaoPhong> dspq = new ArrayList<>();
    int tuychon = -1;
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


    public void CapNhapBanBe()
    {
        database.getReference().child("TroChuyen").child(maso).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dskb.clear();
                ArrayList<String> banbe = new ArrayList<>();
                if(phongChat.getQuanTri() != null)
                    for(String a : phongChat.getQuanTri())
                        banbe.add(a);
                if(phongChat.getThanhVien() != null)
                    for(String a : phongChat.getThanhVien())
                        banbe.add(a);
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getChildrenCount() > 0)
                    {
                        for(NguoiDung ng : nguoiDungs)
                        {
                            if(ng.getMaSo().equals(snapshot1.getKey()))
                            {

                                BanBeTaoPhong kb = new BanBeTaoPhong();
                                if(banbe != null)
                                    if(!banbe.contains(ng.getMaSo())) {
                                        kb.setCheck(false);
                                        kb.setMaso(ng.getMaSo());
                                        kb.setTen(ng.getTen());
                                        dskb.add(kb);
                                    }
                            }

                        }
                    }
                }
                binding.listDSBB.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                ArrayAdapter<BanBeTaoPhong> arrayAdapter
                        = new ArrayAdapter<BanBeTaoPhong>(QuanLyPhongChat.this, android.R.layout.simple_list_item_checked , dskb);
                binding.listDSBB.setAdapter(arrayAdapter);
                binding.txtSearchBb.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        TimKiemList(arrayAdapter,dskb);
                        return false;
                    }
                });

                for(int i=0;i< dskb.size(); i++ )  {
                    binding.listDSBB.setItemChecked(i,dskb.get(i).isCheck());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void CapNhapThanhVien()
    {
        dstv.clear();
        for(NguoiDung ng : nguoiDungs)
        {
            BanBeTaoPhong tv = new BanBeTaoPhong();
            if(phongChat.getThanhVien() != null)
                if(phongChat.getThanhVien().contains(ng.getMaSo()))
                {
                    tv.setCheck(true);
                    tv.setMaso(ng.getMaSo());
                    tv.setTen(ng.getTen());
                    dstv.add(tv);
                }
        }
        binding.listDSBB.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<BanBeTaoPhong> arrayAdapter
                = new ArrayAdapter<BanBeTaoPhong>(QuanLyPhongChat.this, android.R.layout.simple_list_item_checked , dstv);
        binding.listDSBB.setAdapter(arrayAdapter);
        binding.txtSearchBb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                TimKiemList(arrayAdapter,dstv);
                return false;
            }
        });

        for(int i=0;i< dstv.size(); i++ )  {
            binding.listDSBB.setItemChecked(i,dstv.get(i).isCheck());
        }
    }

    public void CapNhapQuanTri(){
        dspq.clear();
        for(NguoiDung ng : nguoiDungs)
        {
            BanBeTaoPhong tv = new BanBeTaoPhong();
            if(phongChat.getThanhVien() != null)
                if(phongChat.getThanhVien().contains(ng.getMaSo()))
                {
                    tv.setCheck(false);
                    tv.setMaso(ng.getMaSo());
                    tv.setTen(ng.getTen());
                    dspq.add(tv);
                }
        }
        binding.listDSBB.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<BanBeTaoPhong> arrayAdapter
                = new ArrayAdapter<BanBeTaoPhong>(QuanLyPhongChat.this, android.R.layout.simple_list_item_checked , dspq);
        binding.listDSBB.setAdapter(arrayAdapter);
        binding.txtSearchBb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                TimKiemList(arrayAdapter,dspq);
                return false;
            }
        });

        for(int i=0;i< dspq.size(); i++ )  {
            binding.listDSBB.setItemChecked(i,dspq.get(i).isCheck());
        }
    }

    public void ThongTinPhong()
    {
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
                    quantri += ng.getTen() + ",";
                }
            if(phongChat.getThanhVien() != null)
                if(phongChat.getThanhVien().contains(ng.getMaSo())){
                    thanhvien.setChucvu(" - Thành Viên");
                    ten += ng.getTen() + ",";
                }
        }
        ten = nguoitao + "\n" + quantri +  "\n" + ten;
        binding.txtThanhVien.setText(ten );

    }

    public void TimKiemList(ArrayAdapter<BanBeTaoPhong> arrayAdapter, ArrayList<BanBeTaoPhong> ds) {
        String chuoi ;

        chuoi = BoDauCach(binding.txtSearchBb.getText().toString().toUpperCase());
        binding.listDSBB.setAdapter(arrayAdapter);
        ArrayList<BanBeTaoPhong> timkiems = new ArrayList<>();
        for (BanBeTaoPhong nd:ds) {
            if(BoDauCach(nd.getTen().toUpperCase()).contains(chuoi) || BoDauCach(nd.getMaso().toUpperCase()).contains(chuoi)){
                timkiems.add(nd);
            }
        }
            ArrayAdapter<BanBeTaoPhong> arrayAdapter1 = new ArrayAdapter<BanBeTaoPhong>(QuanLyPhongChat.this, android.R.layout.simple_list_item_checked , timkiems);
            for(int i=0;i< timkiems.size(); i++ )  {
                binding.listDSBB.setItemChecked(i,timkiems.get(i).isCheck());
                binding.listDSBB.setAdapter(arrayAdapter1);
        }
    }

    public void CapNhapPhong (String maso)
    {
        binding.listTuyChon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tuychon = position;
                if(position == 0)
                {
                    CapNhapBanBe();
                }
                if(position == 1)
                {
                    CapNhapThanhVien();
                }
                if(position == 2)
                {
                    CapNhapQuanTri();
                }

                binding.listDSBB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(tuychon == 0){
                            ArrayList<String> themtv = new ArrayList<>();
                            if(phongChat.getThanhVien() != null)
                            {
                                themtv = phongChat.getThanhVien();
                                themtv.add(dskb.get(position).getMaso());
                            }
                            else
                            {
                                themtv = new ArrayList<>();
                                themtv.add(dskb.get(position).getMaso());
                                phongChat.setThanhVien(themtv);
                            }


                            CapNhapBanBe();
                        }
                        if(tuychon == 1)
                        {
                            ArrayList<String> xoatv = new ArrayList<>();
                            if(phongChat.getThanhVien() != null)
                                for (String maso : phongChat.getThanhVien())
                                    if (!maso.equals(dstv.get(position).getMaso())) {
                                        xoatv.add(maso);
                                    }
                            phongChat.setThanhVien(xoatv);
                            CapNhapThanhVien();
                        }
                        if(tuychon == 2)
                        {
                            CheckedTextView v = (CheckedTextView) view;
                            ArrayList<String> xoatv = new ArrayList<>();
                                if(phongChat.getThanhVien() != null)
                                    for (String maso : phongChat.getThanhVien())
                                        if (!maso.equals(dspq.get(position).getMaso())) {
                                            xoatv.add(maso);
                                        }
                                phongChat.setThanhVien(xoatv);
                                ArrayList<String> themqt = new ArrayList<>();
                                themqt = phongChat.getQuanTri();
                                themqt.add(dspq.get(position).getMaso());
                                CapNhapQuanTri();
                        }
                        ThongTinPhong();

                    }
                });
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuanLyPhongChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = FirebaseDatabase.getInstance();
        maso = Setmaso();

        binding.listTuyChon.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        BanBeTaoPhong themban = new BanBeTaoPhong("1","Thêm Bạn", false);
        BanBeTaoPhong quanlythanhvien = new BanBeTaoPhong("2","Xóa" + "\n"+ "Thành Viên",false);
        BanBeTaoPhong capquyen = new BanBeTaoPhong("3","Cấp Quyền",false);
        BanBeTaoPhong[] tuychon = new BanBeTaoPhong[]{themban,quanlythanhvien, capquyen};
        ArrayAdapter<BanBeTaoPhong> Adaptertuychon
                = new ArrayAdapter<BanBeTaoPhong>(QuanLyPhongChat.this, android.R.layout.simple_list_item_single_choice , tuychon);
        binding.listTuyChon.setAdapter(Adaptertuychon);

        phongChat = (PhongChat) getIntent().getSerializableExtra("PhongChat");

        binding.txtTenPhongChat.setText(phongChat.getTenPhong());
        Glide.with(this).load(phongChat.getLinkAnh())
                .into(binding.anhPhongChat);

        binding.btnreset.setVisibility(View.GONE);


        database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nguoiDungs.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    NguoiDung ng = snapshot1.getValue(NguoiDung.class);
                        if(ng.getMaSo().equals(maso))
                            nguoiDung = ng;
                        nguoiDungs.add(ng);
                }

                    ThongTinPhong();
                    getSupportActionBar().setTitle("QUẢN LÝ PHÒNG CHAT");
                    CapNhapPhong(maso);



                binding.btnThemNhom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(chonAnh!= null && !binding.txtTenPhongChat.getText().toString().equals("") && !binding.txtThanhVien.getText().equals("")) {
                            try {
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
                                                    checkthem = true;
                                                    database.getReference().child("PhongChat").child(phongChat.getMaPhong()).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(checkthem == true) {
                                                                HashMap<String, Object> capnhap = new HashMap<>();
                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                                                String ngaygio = dateFormat.format(new Date().getTime());
                                                                String chuoi = "Vừa cập nhập nhóm";
                                                                capnhap.put("tinCuoi",chuoi);
                                                                capnhap.put("thoiGianTinCuoi",ngaygio);
                                                                capnhap.put("tenPhong",tenPhong);
                                                                capnhap.put("linkAnh",linkAnh);
                                                                capnhap.put("thanhVien",phongChat.getThanhVien());
                                                                capnhap.put("quanTri",phongChat.getQuanTri());

                                                                database.getReference()
                                                                        .child("PhongChat")
                                                                        .child(phongChat.getMaPhong())
                                                                        .updateChildren(capnhap);

                                                                TinNhan tinNhan = new TinNhan(chuoi, nguoiDung.getMaSo(), ngaygio);
                                                                database.getReference().child("PhongChat").child(phongChat.getMaPhong()).child("tinNhan").addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        long stt =  snapshot.getChildrenCount() + 1;
                                                                        sttTin = stt + "";
                                                                        if(checkthem == true)
                                                                        {
                                                                            database.getReference().child("PhongChat")
                                                                                    .child(phongChat.getMaPhong())
                                                                                    .child("tinNhan")
                                                                                    .child(sttTin)
                                                                                    .setValue(tinNhan);
                                                                            chonAnh = null;
                                                                            checkthem = false;
                                                                            finish();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    Toast.makeText(QuanLyPhongChat.this, "CẬP NHẬP THÀNH CÔNG", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });
                            }catch (Exception ex){
                                Toast.makeText(QuanLyPhongChat.this,"TẠO THẤT BẠI",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            if(chonAnh == null){
                                checkthem = true;
                                database.getReference().child("PhongChat").child(phongChat.getMaPhong()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(checkthem == true) {
                                            String tenPhong = binding.txtTenPhongChat.getText().toString();
                                            HashMap<String, Object> capnhap = new HashMap<>();
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                            String ngaygio = dateFormat.format(new Date().getTime());
                                            String chuoi = "Vừa cập nhập nhóm";
                                            capnhap.put("tinCuoi",chuoi);
                                            capnhap.put("thoiGianTinCuoi",ngaygio);
                                            capnhap.put("tenPhong",tenPhong);
                                            capnhap.put("thanhVien",phongChat.getThanhVien());
                                            capnhap.put("quanTri",phongChat.getQuanTri());
                                            database.getReference()
                                                    .child("PhongChat")
                                                    .child(phongChat.getMaPhong())
                                                    .updateChildren(capnhap);

                                            TinNhan tinNhan = new TinNhan(chuoi, nguoiDung.getMaSo(), ngaygio);

                                            database.getReference().child("PhongChat").child(phongChat.getMaPhong()).child("tinNhan").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    long stt =  snapshot.getChildrenCount() + 1;
                                                    sttTin = stt + "";
                                                    if(checkthem == true)
                                                    {
                                                        database.getReference().child("PhongChat")
                                                                .child(phongChat.getMaPhong())
                                                                .child("tinNhan")
                                                                .child(sttTin)
                                                                .setValue(tinNhan);
                                                        chonAnh = null;
                                                        checkthem = false;
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            if(binding.txtThanhVien.getText().equals(""))
                                Toast.makeText(QuanLyPhongChat.this,"Vui Lòng Thêm Thành Viên Phòng Chat",Toast.LENGTH_SHORT).show();
                            if(binding.txtTenPhongChat.getText().toString().equals(""))
                                Toast.makeText(QuanLyPhongChat.this,"Vui Lòng Đặt Tên Phòng Chat",Toast.LENGTH_SHORT).show();

                        }
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