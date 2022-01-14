package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.doanjava.messbcode.Adapters.ChuyenDoiTinNhanDiaDiem;
import com.doanjava.messbcode.Adapters.ChuyenDoiTrangThai;
import com.doanjava.messbcode.GuiThongBao;
import com.doanjava.messbcode.Models.CauChuyen;
import com.doanjava.messbcode.Models.DiaDiemYeuThich;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.TinNhan;
import com.doanjava.messbcode.Models.TinNhanChung;
import com.doanjava.messbcode.Models.TrangThaiNguoiDung;
import com.doanjava.messbcode.Models.ViTriGPS;
import com.doanjava.messbcode.databinding.ActivityNhomNhanTinBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NhanTinDiaDiem extends AppCompatActivity implements OnMapReadyCallback {

    ActivityNhomNhanTinBinding binding;
    ChuyenDoiTinNhanDiaDiem chuyenDoiTinNhan = new ChuyenDoiTinNhanDiaDiem();
    ChuyenDoiTrangThai chuyenDoiTrangThai = new ChuyenDoiTrangThai();
    ArrayList<TinNhan> tinNhans = new ArrayList<>();
    GoogleMap map;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String sttTrangThai;
    String maSoNguoiGui;
    int max,maxn,maxcc;
    String sttTin;
    String maNhom = "0";
    NguoiDung nguoiDung = new NguoiDung();
    ArrayList<NguoiDung> listToanBoNguoiDung = new ArrayList<>();
    ArrayList<NguoiDung> listNguoiDung = new ArrayList<>();
    ArrayList<NguoiDung> listQuaKhu = new ArrayList<>();
    ArrayList<NguoiDung> listHienTai = new ArrayList<>();
    ArrayList<TrangThaiNguoiDung> trangThaiNguoiDungs = new ArrayList<>();
    List<Address> addressList = new ArrayList<>();
    DiaDiemYeuThich diaDiemChat = new DiaDiemYeuThich();
    NguoiDung nguoigui = new NguoiDung();

    long tam = 0;

    public String CatDiaChi(String Diachi)
    {
        String[] tenduong = Diachi.split(",");;
        try {
            if (tenduong[0].equals("Đường chưa đặt tên"))
                return tenduong[1];
        }catch (Exception ex){}
        return tenduong[0];
    }

    public int TimMaxCauTruyen (String maNhom)
    {

        database.getReference().child("Nhom").child(maNhom).child("CauChuyen").child("NguoiDung: " + maSoNguoiGui).child("trangThai").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                maxcc = 0;
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    if(Integer.parseInt(snapshot1.getKey()) > maxcc)
                        maxcc = Integer.parseInt(snapshot1.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return maxcc + 1;
    }

    public int TimMaxNhom ()
    {

        database.getReference().child("Nhom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                maxn = 0;
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    if(Integer.parseInt(snapshot1.getKey()) > maxn)
                        maxn = Integer.parseInt(snapshot1.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return maxn + 1;
    }

    public Address ToaDosangViTri (double latitude,double longitude)
    {
        List<Address> addressList = new ArrayList<>();
        addressList.clear();
        Geocoder geocoder = new Geocoder(NhanTinDiaDiem.this);
        try {
            addressList = geocoder.getFromLocation(latitude,longitude,1);
            Address address = addressList.get(0);
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String ChuyenDoiDau(String str) {
        str = str.replaceAll("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ", "a");
        str = str.replaceAll("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ", "e");
        str = str.replaceAll("ì|í|ị|ỉ|ĩ", "i");
        str = str.replaceAll("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ", "o");
        str = str.replaceAll("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ", "u");
        str = str.replaceAll("ỳ|ý|ỵ|ỷ|ỹ", "y");
        str = str.replaceAll("đ", "d");

        str = str.replaceAll("À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ", "A");
        str = str.replaceAll("È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ", "E");
        str = str.replaceAll("Ì|Í|Ị|Ỉ|Ĩ", "I");
        str = str.replaceAll("Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ", "O");
        str = str.replaceAll("Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ", "U");
        str = str.replaceAll("Ỳ|Ý|Ỵ|Ỷ|Ỹ", "Y");
        str = str.replaceAll("Đ", "D");
        return str.toUpperCase();
    }

    public boolean SoSanhDiaDiem(Address diadiem1, Address diadiem2)
    {
        if(diadiem1 != null && diadiem2 != null) {
            String diachi1 = ChuyenDoiDau(CatDiaChi(diadiem1.getAddressLine(0)));
            String diachi2 = ChuyenDoiDau(CatDiaChi(diadiem2.getAddressLine(0)));
            Log.v("Log1", diachi1 + "");
            Log.v("Log2", diachi2 + "");
            if (diachi1.length() > diachi2.length()) {
                if (diachi1.contains(diachi2) && ChuyenDoiDau(diadiem1.getAdminArea()).equals(ChuyenDoiDau(diadiem2.getAdminArea())) && ChuyenDoiDau(diadiem1.getCountryName()).equals(ChuyenDoiDau(diadiem2.getCountryName())))
                    return true;
            } else {
                if (diachi2.contains(diachi1) && ChuyenDoiDau(diadiem1.getAdminArea()).equals(ChuyenDoiDau(diadiem2.getAdminArea())) && ChuyenDoiDau(diadiem1.getCountryName()).equals(ChuyenDoiDau(diadiem2.getCountryName())))
                    return true;
            }
        }
        return false;
    }

    public int TimMax (String maNhom)
    {
        database.getReference().child("Nhom").child(maNhom).child("tinNhan").addValueEventListener(new ValueEventListener() {
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
    public void onCreate(Bundle savedInstanceState) {
        binding = ActivityNhomNhanTinBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        maSoNguoiGui = getIntent().getStringExtra("maNguoiGui");
        diaDiemChat = (DiaDiemYeuThich) getIntent().getSerializableExtra("chatDiaDiem");
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        LinearLayoutManager layoutManager = new LinearLayoutManager(NhanTinDiaDiem.this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(layoutManager);

        tinNhans = new ArrayList<>();
        chuyenDoiTinNhan = new ChuyenDoiTinNhanDiaDiem(NhanTinDiaDiem.this,maSoNguoiGui, tinNhans);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(NhanTinDiaDiem.this));
        binding.recyclerView.setAdapter(chuyenDoiTinNhan);

        //Lay DS Nguoi Dung*
        database.getReference().child("ViTri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    listToanBoNguoiDung.clear();
                    listNguoiDung.clear();
                    tam = snapshot.getChildrenCount();
                    for(DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot snapshot2 : snapshot.getChildren()) {
                                        NguoiDung tbnd = snapshot2.getValue(NguoiDung.class);
                                    if(!listToanBoNguoiDung.contains(tbnd))
                                        listToanBoNguoiDung.add(tbnd);
                                    if (snapshot1.getKey().equals(snapshot2.getKey())) {
                                        nguoiDung = snapshot2.getValue(NguoiDung.class);
                                        ViTriGPS vitri = snapshot1.getValue(ViTriGPS.class);
                                        nguoiDung.setLatitude(vitri.latitude);
                                        nguoiDung.setLongitude(vitri.longitude);
                                        listNguoiDung.add(nguoiDung);
                                        if(listNguoiDung.size() == tam) {
                                            onMapReady(map);
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
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
        String maHoatDong = "NguoiDung " + maSoNguoiGui;
        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Trực tuyến");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String maHoatDong = "NguoiDung " + maSoNguoiGui;
        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Ngoại tuyến");
    }

    //Them Anh

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Trang Thai
        if (requestCode == 11) {
            if (data.getData() != null) {
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
                        if (task.isSuccessful()) {
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
                                    String maso = "NguoiDung: " + maSoNguoiGui;

                                    database.getReference()
                                            .child("Nhom")
                                            .child(maNhom)
                                            .child("CauChuyen")
                                            .child(maso)
                                            .updateChildren(obj);

                                    maxcc = TimMaxCauTruyen(maNhom);
                                    sttTrangThai = "" + maxcc;
                                    database.getReference().
                                             child("Nhom")
                                            .child(maNhom)
                                            .child("CauChuyen")
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
        // Theem Anh
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
                                        sttTin = "" + TimMax(maNhom);
                                        String tinTxt = binding.messageBox.getText().toString();

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String ngaygio = dateFormat.format(new Date().getTime());
                                        TinNhan tinNhan = new TinNhan(tinTxt, maSoNguoiGui, ngaygio);
                                        tinNhan.setTinNhan("HinhAnh");
                                        tinNhan.setLinkAnhGui(linkAnh);
                                        binding.messageBox.setText("");

                                        database.getReference().child("Nhom")
                                                .child(maNhom)
                                                .child("tinNhan")
                                                .child(sttTin)
                                                .setValue(tinNhan);

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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        dialog = new ProgressDialog( this);
            for (NguoiDung ng : listNguoiDung)
            {
                if(ng.getMaSo().equals(maSoNguoiGui)) {
                    nguoigui = new NguoiDung(ng.getMaSo(), ng.getTen(), ng.getEmail(), ng.getMatKhau(), ng.getGioiTinh(), ng.getAnhDaiDien(), ng.getNgaySinh(), ng.getSoDienThoai(), ng.getLatitude(), ng.getLongitude());
                    if(diaDiemChat != null) {
                        nguoigui.setLatitude(diaDiemChat.getLatitude());
                        nguoigui.setLongitude(diaDiemChat.getLongitude());
                    }
                }
            }
            int soluong = 0;
            listHienTai.clear();
            for(NguoiDung ng: listNguoiDung)
            {
                if(!ng.getMaSo().equals(maSoNguoiGui) && SoSanhDiaDiem(ToaDosangViTri(ng.getLatitude(),ng.getLongitude()),ToaDosangViTri(nguoigui.getLatitude(),nguoigui.getLongitude())))
                {
                    soluong++;
                    listHienTai.add(ng);
                }
            }
        /*for (NguoiDung ngqk : listQuaKhu) {
            if (!listNguoiDung.contains(ngqk)) {
                String vuara = ngqk.getTen().toUpperCase() + " vừa rời nơi này";
                Toast.makeText(NhomNhanTin.this,vuara,Toast.LENGTH_SHORT).show();
            }
        }*/
        for (NguoiDung nght : listHienTai) {
            if (!listQuaKhu.contains(nght)) {
                String vuavao = nght.getTen().toUpperCase() + " đang ở nơi này";
                Toast toast =  Toast.makeText(NhanTinDiaDiem.this, vuavao, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 2, 2);
                toast.show();
            }
        }
        listQuaKhu = listHienTai;
        Geocoder geocoder = new Geocoder(NhanTinDiaDiem.this);
        try {
            addressList.clear();
            addressList = geocoder.getFromLocation(nguoigui.getLatitude(), nguoigui.getLongitude(), 1);
            Address address = addressList.get(0);
            if (address != null)
                if(diaDiemChat != null)
                getSupportActionBar().setTitle("ĐC: " + diaDiemChat.getTenDiaDiem().toUpperCase() +" - "+ CatDiaChi(address.getAddressLine(0)));
                else
                getSupportActionBar().setTitle("ĐC:"+ CatDiaChi(address.getAddressLine(0)));
        }catch (Exception ex){}

        binding.txtSoNguoi.setText("Vị trí hiện có " + soluong + " người khác.");
        binding.txtSoNguoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  binding.statusList.setVisibility(View.GONE);
            }
        });
        database.getReference().child("Nhom").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() == 0)
                    {
                        ViTriGPS vt = new ViTriGPS(nguoigui.getLatitude(), nguoigui.getLongitude());
                        maNhom = TimMaxNhom() + "";
                        database.getReference().child("Nhom").child(maNhom).setValue(vt);
                    }
                    else {
                        boolean check = false;
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            TinNhanChung toadoNhom = snapshot1.getValue(TinNhanChung.class);
                            if(SoSanhDiaDiem(ToaDosangViTri(toadoNhom.getLatitude(),toadoNhom.getLongitude()),ToaDosangViTri(nguoigui.getLatitude(),nguoigui.getLongitude()))) {
                                maNhom = snapshot1.getKey();
                                check = true;
                            }
                        }
                        if (!check) {
                            ViTriGPS vt = new ViTriGPS(nguoigui.getLatitude(), nguoigui.getLongitude());
                            maNhom = TimMaxNhom() + "";
                            database.getReference().child("Nhom").child(maNhom).setValue(vt);
                        }
                    }

                    chuyenDoiTrangThai = new ChuyenDoiTrangThai(NhanTinDiaDiem.this, trangThaiNguoiDungs);
                    chuyenDoiTrangThai.setNguoiDung(nguoiDung);
                    chuyenDoiTrangThai.setNguoiDungs(listToanBoNguoiDung);
                    binding.statusList.setAdapter(chuyenDoiTrangThai);
                    binding.statusList.showShimmerAdapter();
                    database.getReference().child("Nhom").child(maNhom).child("CauChuyen").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                trangThaiNguoiDungs.clear();
                                for(DataSnapshot storySnapShot : snapshot.getChildren()) {
                                    TrangThaiNguoiDung trangThai = new TrangThaiNguoiDung();
                                    trangThai.setNguoiDang(storySnapShot.child("nguoiDang").getValue(String.class));
                                    trangThai.setAnhDaiDien(storySnapShot.child("anhDaiDien").getValue(String.class));
                                    trangThai.setCapNhatCuoi(storySnapShot.child("capNhatCuoi").getValue(String.class));

                                    ArrayList<CauChuyen> cauChuyens = new ArrayList<>();

                                    for(DataSnapshot statusSnapshot : storySnapShot.child("trangThai").getChildren()) {
                                        CauChuyen sampleStatus = statusSnapshot.getValue(CauChuyen.class);
                                        cauChuyens.add(sampleStatus);
                                    }

                                    trangThai.setCauChuyens(cauChuyens);
                                    trangThaiNguoiDungs.add(trangThai);
                                }
                                binding.statusList.hideShimmerAdapter();
                                chuyenDoiTrangThai.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    chuyenDoiTinNhan.setMaNhom(maNhom);
                    chuyenDoiTinNhan.setNguoiDungs(listToanBoNguoiDung);
                    chuyenDoiTinNhan.setListHienTai(listHienTai);
                    database.getReference().child("Nhom").child(maNhom).child("tinNhan")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    tinNhans.clear();
                                    String id = "";
                                    for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        TinNhan tinNhan =  snapshot1.getValue(TinNhan.class);
                                        tinNhan.setMaTin(snapshot1.getKey());
                                        if (!tinNhan.getMaTin().equals(id)) {
                                            tinNhans.add(tinNhan);
                                            id = tinNhan.getMaTin();
                                        }
                                    }
                                    chuyenDoiTinNhan.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    sttTin = "" + TimMax(maNhom);
                    binding.sentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String tinTxt = binding.messageBox.getText().toString();
                            if(!tinTxt.isEmpty()){

                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                String ngaygio = dateFormat.format(new Date().getTime());
                                sttTin = "" + TimMax(maNhom);
                                TinNhan tinNhan = new TinNhan(tinTxt, maSoNguoiGui, ngaygio);
                                binding.messageBox.setText("");
                                database.getReference().child("Nhom")
                                        .child(maNhom)
                                        .child("tinNhan")
                                        .child(sttTin)
                                        .setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                         for(NguoiDung ng : listHienTai)
                                        {
                                            if(!ng.getMaSo().equals(nguoigui.getMaSo()))
                                            {
                                                  GuiThongBao.GuiThongBao(nguoigui.getTen(),tinNhan.getTinNhan(),ng.getToken(), NhanTinDiaDiem.this);
                                            }
                                        }
                                    }
                                });
                                if(tinNhans.size() > 1)
                                    binding.recyclerView.smoothScrollToPosition(tinNhans.size() -1);
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

                    binding.camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent,11);
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
