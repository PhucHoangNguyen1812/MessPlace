package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Adapters.ChuyenDoiDiaDiem;
import com.doanjava.messbcode.Adapters.DiaDiemYeuThichMapAPI;
import com.doanjava.messbcode.Adapters.MyClusterManagerRenderer;
import com.doanjava.messbcode.Adapters.NguoiDungMapAPI;
import com.doanjava.messbcode.Models.ClusterMarker;
import com.doanjava.messbcode.Models.DiaDiemYeuThich;
import com.doanjava.messbcode.Models.LuotYeuThich;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.ViTriGPS;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityDrawerBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Main_Map extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener  {

    ActivityDrawerBinding binding;
    GoogleMap map;
    SupportMapFragment mapFragment;
    public LocationManager manager;
    public final int MIN_TIME = 1000;
    private final int MIN_DISTANCE = 1;
    TextView txtTenDiaDanh,textDS,timkiemlist,txtmapker0,txtmapker1,txtmapker2,txtmapker3;
    SearchView timkiemdiadiem;
    LatLng vitribanbe,vitritoi,vitritimkiem,viTriYeuThich = new LatLng(0,0);
    View mapview,viewthongtin,viewthemdiadanh;
    Button btntimkiemlist,btnshowmap,btnshowthongtin,btnMaper1,btnIcon,btnOke;;
    boolean showmap,showtimkiem,showthongtin = true;
    FirebaseDatabase database;
    ListView listView;
    String maso,sdt,location = "";
    int start,sumnguoi,max,tam,sodiadiemyeuthich = 0;
    int tuychon = 1;
    List<Address> addressList = new ArrayList<>();
    NguoiDungMapAPI nguoiDungMapAPI;
    DiaDiemYeuThichMapAPI diaDiemYeuThichMapAPI;
    List<NguoiDung> listNguoiDung = new ArrayList<>();
    List<DiaDiemYeuThich> listDiaDiemYeuThich = new ArrayList<>();
    List<DiaDiemYeuThich> listDiaDiemKhac = new ArrayList<>();
    DiaDiemYeuThich diadiemsearch = new DiaDiemYeuThich();
    ImageView avatarMaper;
    private Intent chonAnh;
    NguoiDung nguoiDung = new NguoiDung();
    String DiaDiemHienTai = "";
    String masonguoixem = "";
    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    NguoiDung nguoiGui = new NguoiDung();

    public String Setmaso()
    {
        maso = getIntent().getStringExtra("maso");
        return maso;
    }

    public String CatDiaChi(String Diachi)
    {
        String[] tenduong = Diachi.split(",");;
        return tenduong[0];
    }

    public Double SoSanhToaDo(Double a,Double b) {
            if(a.toString().length() > b.toString().length())
               a = Double.parseDouble(a.toString().substring(0,b.toString().length()));
        return a;
    }

    public boolean SoSanhDiaDiem(Address diadiem1, Address diadiem2)
    {
        String diachi1 = CatDiaChi(diadiem1.getAddressLine(0));
        String diachi2 = CatDiaChi(diadiem2.getAddressLine(0));
        if(diachi1.length() > diachi2.length())
        {
            if(diachi1.contains(diachi2) && diadiem1.getLocality().equals(diadiem2.getLocality())
                    && diadiem1.getAdminArea().equals(diadiem2.getAdminArea()) && diadiem1.getCountryName().equals(diadiem2.getCountryName()))
                return true;
        }
        else
        {
            if(diachi2.contains(diachi1) && diadiem1.getLocality().equals(diadiem2.getLocality())
                    && diadiem1.getAdminArea().equals(diadiem2.getAdminArea()) && diadiem1.getCountryName().equals(diadiem2.getCountryName()))
                return true;
        }
        return false;
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
        String chuoi = BoDauCach(timkiemlist.getText().toString().toUpperCase());
        if (tuychon == 1) {
            listView.setAdapter(nguoiDungMapAPI);
            NguoiDungMapAPI listtimkiem = new NguoiDungMapAPI(Main_Map.this, R.layout.list_thongtin_user);
            for (int i = 0; i < listView.getCount(); i++) {
                NguoiDung nguoiDung = ((NguoiDung) listView.getItemAtPosition(i));
                addressList.clear();
                Geocoder geocoder = new Geocoder(Main_Map.this);
                try {
                    addressList = geocoder.getFromLocation(nguoiDung.getLatitude(), nguoiDung.getLongitude(), 1);
                    Address address = addressList.get(0);
                    if (address != null)
                        if (BoDauCach(nguoiDung.getMaSo().toUpperCase()).contains(chuoi) || BoDauCach(nguoiDung.getTen().toUpperCase()).contains(chuoi))
                    listtimkiem.add(nguoiDung);
                }catch (Exception ex){}

            }
            listView.setAdapter(listtimkiem);
            SuKienListView(listtimkiem,null);
        }
        else {
            listView.setAdapter(diaDiemYeuThichMapAPI);
            DiaDiemYeuThichMapAPI listtimkiem = new DiaDiemYeuThichMapAPI(Main_Map.this, R.layout.list_thongtin_user);
            for (int i = 0; i < listView.getCount(); i++) {
                DiaDiemYeuThich diadiem = ((DiaDiemYeuThich) listView.getItemAtPosition(i));
                addressList.clear();
                Geocoder geocoder = new Geocoder(Main_Map.this);
                try {
                    addressList = geocoder.getFromLocation(diadiem.getLatitude(), diadiem.getLongitude(), 1);
                    Address address = addressList.get(0);
                    if (address != null)
                        if (BoDauCach(diadiem.getTenDiaDiem().toUpperCase()).contains(chuoi) || BoDauCach(address.getAddressLine(0).toUpperCase()).contains(chuoi))
                            listtimkiem.add(diadiem);
                }catch (Exception ex){}
            }
            listView.setAdapter(listtimkiem);
            SuKienListView(null,listtimkiem);
        }
    }

    public int TimMaxDiaDiem ()
    {

        database.getReference().child("DiaDiemYeuThich").child("NguoiDung: " + maso).addValueEventListener(new ValueEventListener() {
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
        CapQuyenGPS();
        super.onCreate(savedInstanceState);
        binding = ActivityDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        final  DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        binding.navigationView.bringToFront();
        binding.imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        binding.navigationView.setItemIconTintList(null);
        NavController navController = Navigation.findNavController(this,R.id.navHostFragment);
        NavigationUI.setupWithNavController(binding.navigationView, navController);


    }

    @Override
    protected void onResume() {
        super.onResume();
        CapQuyenGPS();
        location = getIntent().getStringExtra("timkiem");
        if(location != null)
        { try {
            binding.svLocation.setQuery(location, true);
            Search(location);
        }catch (Exception ex){}
        }


        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId())
                {
                    case R.id.capnhapthongtin:
                        intent = new Intent(Main_Map.this, ThongTinTaiKhoan.class);
                        intent.putExtra("nguoiDung",nguoiGui);
                        startActivity(intent);
                        break;
                    case R.id.location:
                        GanViTriLenMap(0,vitritoi,null,null,null);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(vitritoi,10));
                        break;
                    case R.id.goiy:
                        intent = new Intent(Main_Map.this, GoiYDiaDiem.class);
                        intent.putExtra("maso", maso);
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        database.getReference().child("ViTri").child(maso).setValue(null);
                        finish();
                        break;
                    case R.id.mess:
                        intent = new Intent(Main_Map.this, MainActivity.class);
                        intent.putExtra("phoneNumber",sdt);
                        intent.putExtra("maso", maso);
                        startActivity(intent);
                        break;
                    case R.id.diadiemchat:
                        intent = new Intent(Main_Map.this, DiaDiemChat.class);
                        intent.putExtra("phoneNumber",sdt);
                        intent.putExtra("maso", maso);
                        startActivity(intent);
                        break;
                    case R.id.phongchat:
                        intent = new Intent(Main_Map.this, PhongChatBanBe.class);
                        intent.putExtra("maso", maso);
                        startActivity(intent);
                        break;
                    case R.id.ketban:
                        maso = Setmaso();
                        intent = new Intent(Main_Map.this,ThemBanBe.class);
                        intent.putExtra("maso",maso);
                        startActivity(intent);
                }
                return false;
            }
        });

        KhaiBaoButtonList();
        SearchMap();
        maso = Setmaso();
        nguoiDungMapAPI = new NguoiDungMapAPI(Main_Map.this, R.layout.list_thongtin_user);
        diaDiemYeuThichMapAPI = new DiaDiemYeuThichMapAPI(Main_Map.this, R.layout.list_thongtin_user);
        //Hiện Thông Tin
        viewthongtin.setVisibility(View.VISIBLE);
        viewthongtin.animate().translationX(370);
        showthongtin = false;
        AnHienListDS(0);

        //Lay DS BAN BE
        database.getReference().child("TroChuyen").child(maso).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dembanbe = 0;
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getChildrenCount() > 0)
                        dembanbe++;
                    TextView txtbanbepf = findViewById(R.id.txtfriend);
                    txtbanbepf.setText(dembanbe + "");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Lay DS Nguoi Dung
        database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {    listNguoiDung.clear();
                try {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        database.getReference().child("ViTri").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    if (snapshot2.getKey().equals(snapshot1.getKey())) {
                                        nguoiDung = snapshot1.getValue(NguoiDung.class);
                                        ViTriGPS vitri = snapshot2.getValue(ViTriGPS.class);
                                        nguoiDung.setLatitude(vitri.latitude);
                                        nguoiDung.setLongitude(vitri.longitude);
                                        listNguoiDung.add(nguoiDung);
                                        if(snapshot2.getKey().equals(maso))
                                            vitritoi = new LatLng(nguoiDung.getLatitude(),nguoiDung.getLongitude());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    if (tuychon == 1)
                        onMapReady(map);
                } catch (Exception ex) { }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Lấy DS Địa Điểm Khác
        database.getReference().child("DiaDiemYeuThich").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDiaDiemKhac.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                   for(DataSnapshot snapshot2 : snapshot1.getChildren())
                   {
                           DiaDiemYeuThich diaDiemYeuThich = snapshot2.getValue(DiaDiemYeuThich.class);
                           diaDiemYeuThich.setMaDiaDiem(snapshot2.getKey());
                           listDiaDiemKhac.add(diaDiemYeuThich);
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Lấy Lượt Yêu Thích

        database.getReference().child("LuotYeuThich").child("NguoiDung: " + maso).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int soluotyeuthich = 0;
                if(snapshot.exists()) {
                    LuotYeuThich dsyeuthich = snapshot.getValue(LuotYeuThich.class);
                    if (dsyeuthich != null) {
                        soluotyeuthich = dsyeuthich.getNguoiYeuThich().size();
                    }
                }
                TextView txtYeuThichpf = findViewById(R.id.txtfavorite);
                txtYeuThichpf.setText(soluotyeuthich + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Lấy DS Địa Điểm
        database.getReference().child("DiaDiemYeuThich").child("NguoiDung: " + maso).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                diaDiemYeuThichMapAPI.clear();
                sodiadiemyeuthich = 0;
                listDiaDiemYeuThich.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        DiaDiemYeuThich diaDiemYeuThich = snapshot1.getValue(DiaDiemYeuThich.class);
                        diaDiemYeuThich.setMaDiaDiem(snapshot1.getKey());
                        listDiaDiemYeuThich.add(diaDiemYeuThich);
                        diaDiemYeuThichMapAPI.add(diaDiemYeuThich);
                        sodiadiemyeuthich ++;
                }

                if(tuychon == 2)
                {
                    textDS.setText("BẠN CÓ " + sodiadiemyeuthich + " ĐỊA ĐIỂM YÊU THÍCH");
                    listView.setAdapter(diaDiemYeuThichMapAPI);
                    ShowDiaDiemYeuThich();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        SuKienListView(nguoiDungMapAPI,diaDiemYeuThichMapAPI);


        XuLyButton();

        BottomNavigationView btnNaviView = findViewById(R.id.tuychon);
        btnNaviView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case  R.id.tcmap:
                        try{
                        tuychon = 1;
                        onMapReady(map);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(vitritoi,10));
                        txtmapker0.setText("BẠN");
                        maso = Setmaso();
                        for (NguoiDung ng : listNguoiDung) {
                            if(ng.getMaSo().equals(maso)) {
                                txtmapker1.setText("ID: " + maso);
                                addressList.clear();
                                avatarMaper = findViewById(R.id.viewAnhDaiDienMapker);
                                Glide.with(Main_Map.this).load(ng.getAnhDaiDien())
                                        .placeholder(R.drawable.avatar)
                                        .into(avatarMaper);
                                Geocoder geocoder = new Geocoder(Main_Map.this);
                                    addressList = geocoder.getFromLocation(vitritoi.latitude,vitritoi.longitude,1);
                                Address address = addressList.get(0);
                                if (address != null) {
                                    txtmapker2.setText("Đang Đứng Tại: " + "\n" + address.getAddressLine(0));
                                }

                            }
                        }
                        btnMaper1.setVisibility(View.INVISIBLE);
                        AnHien(viewthemdiadanh,0,-500);
                        AnHien(btnIcon,0,-500);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.tcdiadiemyeuthich:
                        tuychon = 2;
                        maso = Setmaso();
                        txtmapker0.setText("BẠN");
                        textDS.setText("BẠN CÓ " + sodiadiemyeuthich + " ĐỊA ĐIỂM YÊU THÍCH");
                        listView.setAdapter(diaDiemYeuThichMapAPI);
                        ShowDiaDiemYeuThich();
                        for (NguoiDung ng : listNguoiDung) {
                            if(ng.getMaSo().equals(maso)) {
                                txtmapker1.setText("ID: " + maso);
                                addressList.clear();
                                avatarMaper = findViewById(R.id.viewAnhDaiDienMapker);
                                Glide.with(Main_Map.this).load(ng.getAnhDaiDien())
                                        .placeholder(R.drawable.avatar)
                                        .into(avatarMaper);
                                Geocoder geocoder = new Geocoder(Main_Map.this);
                                try {
                                    addressList = geocoder.getFromLocation(vitritoi.latitude,vitritoi.longitude,1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Address address = addressList.get(0);
                                if (address != null) {
                                    txtmapker2.setText("Đang Đứng Tại: " + "\n" + address.getAddressLine(0));
                                }
                            }
                        }
                        btnMaper1.setVisibility(View.INVISIBLE);
                        AnHien(viewthemdiadanh,0,-500);
                        AnHien(btnIcon,0,-500);
                        AnHienListDS(1);
                        if(!showthongtin)
                        {
                            viewthongtin.animate().translationX(-370);
                            showthongtin = true;
                            AnHien(viewthemdiadanh,0,-500);
                            AnHien(btnIcon,0,-500);
                        }

                        break;
                    case  R.id.tcchat:
                        maso = Setmaso();
                        Intent intent = new Intent(Main_Map.this, NhanTinDiaDiem.class);
                        intent.putExtra("maNguoiGui",maso);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
        String maHoatDong = "NguoiDung " + maso;
        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Trực tuyến");

    }

    private void SuKienListView(NguoiDungMapAPI nguoidung,DiaDiemYeuThichMapAPI diadiem) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if(tuychon == 1) {
                vitribanbe = new LatLng(nguoidung.getItem(position).getLatitude(),nguoidung.getItem(position).getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(vitribanbe, 10));
                viewthongtin.animate().translationX(370);
                showthongtin = false;
                if(showmap)
                    AnHienListDS(0);
                txtmapker0.setText(nguoidung.getItem(position).getTen());
                txtmapker1.setText("ID: " + nguoidung.getItem(position).getMaSo());
                masonguoixem = nguoidung.getItem(position).getMaSo();
                addressList.clear();
                avatarMaper = findViewById(R.id.viewAnhDaiDienMapker);
                Glide.with(Main_Map.this).load(nguoidung.getItem(position).getAnhDaiDien())
                        .placeholder(R.drawable.avatar)
                        .into(avatarMaper);
                Geocoder geocoder = new Geocoder(Main_Map.this);
                try {
                    addressList = geocoder.getFromLocation(nguoidung.getItem(position).getLatitude(), nguoidung.getItem(position).getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                if (address != null) {
                    txtmapker2.setText("Đang Đứng Tại: " + "\n" + address.getAddressLine(0));
                    btnMaper1.setVisibility(View.VISIBLE);
                    btnMaper1.setText("Chi Tiết");
                }
            }
            if(tuychon == 2)
            {

                viTriYeuThich = new LatLng(diadiem.getItem(position).getLatitude(),diadiem.getItem(position).getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(viTriYeuThich, 10));
                viewthongtin.animate().translationX(370);
                showthongtin = false;
                if(showmap)
                    AnHienListDS(0);
                addressList.clear();
                Geocoder geocoder = new Geocoder(Main_Map.this);
                try{
                    addressList = geocoder.getFromLocation(diadiem.getItem(position).getLatitude(),diadiem.getItem(position).getLongitude(),1);
                    Address address = addressList.get(0);
                    if(address != null) {
                        Glide.with(Main_Map.this).load(diadiem.getItem(position).getAnhDiaDiem())
                                .placeholder(R.drawable.anhdd)
                                .into(avatarMaper);
                        txtmapker0.setText("ĐỊA ĐIỂM \n YÊU THÍCH");
                        txtmapker1.setVisibility(View.VISIBLE);
                        txtmapker1.setText(diadiem.getItem(position).getTenDiaDiem());
                        txtmapker2.setText("Nằm ở: " + address.getAddressLine(0));
                        txtmapker3.setText(diadiem.getItem(position).getLatitude() + "," + diadiem.getItem(position).getLongitude());
                        btnMaper1.setVisibility(View.VISIBLE);
                        btnMaper1.setText("XÓA");
                    }
                }catch (Exception e) {
                }

            }
        });
    }


    private void GanViTriLenMap(int i, LatLng vitri, String timkiem, String ten,String avatar) {
        addressList.clear();
        Geocoder geocoder = new Geocoder(Main_Map.this);
        // 0 Của Tôi, 1 Tìm Kiếm, 2 Bạn Bè, 3 Yêu Thích
        try{
            if(i == 0 || i == 2 || i == 3)
                addressList = geocoder.getFromLocation(vitri.latitude,vitri.longitude,1);
            if(i == 1 && timkiem != null)
                addressList = geocoder.getFromLocationName(timkiem,1);
            if(i == 1 && vitri != null)
                addressList = geocoder.getFromLocation(vitri.latitude,vitri.longitude,1);

            Address address = addressList.get(0);
            if(address != null) {
                vitri = new LatLng(address.getLatitude(), address.getLongitude());
                if (i == 0)
                    map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(vitri).title("Bạn tại:").snippet(CatDiaChi(address.getAddressLine(0))));
                if (i == 1) {
                    vitritimkiem = new LatLng(address.getLatitude(), address.getLongitude());
                    map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).position(vitritimkiem).title("Search:").snippet(CatDiaChi(address.getAddressLine(0))));
                }
                if (i == 2) {
                    if (DiaDiemHienTai.equals(address.getAddressLine(0)))
                        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(vitri).title(ten + " :").snippet("Gần Bạn"));
                    else
                        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(vitri).title(ten + " :").snippet("Xa Bạn"));
                }
                if (i == 3) {
                    map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).position(vitri).title("Địa điểm yêu thích:").snippet(CatDiaChi(address.getAddressLine(0))));
                }
            }
        }catch (Exception e) {
        }
    }

    public void ShowDiaDiemYeuThich() {
        maso = Setmaso();
        map.clear();
                for (DiaDiemYeuThich yt : listDiaDiemYeuThich) {
                    try {
                        viTriYeuThich = new LatLng(yt.getLatitude(), yt.getLongitude());
                        if(tuychon == 2)
                            GanViTriLenMap(3, viTriYeuThich, null,null,null);
                    } catch (Exception ex) {
                        ex.getMessage();
                    }
                }
                GanViTriLenMap(0,vitritoi,null,null,null);
                if(location != "")
                    GanViTriLenMap(1,vitritimkiem,null,null,null);
    }



    public void SearchMap(){
        timkiemdiadiem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                location = timkiemdiadiem.getQuery().toString();
                Search(location);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void Search(String location)
    {
        tam = 0;
        addressList.clear();
        Geocoder geocoder = new Geocoder(Main_Map.this);
        try {
            Address address;
            if(!location.toLowerCase().equals("me") && !location.toLowerCase().equals("tôi")) {
                addressList = geocoder.getFromLocationName(location, 1);
                address = addressList.get(0);
                GanViTriLenMap(1, null, location, null,null);

            }else {
                addressList = geocoder.getFromLocation(vitritoi.latitude, vitritoi.longitude, 1);
                address = addressList.get(0);
                GanViTriLenMap(1, vitritoi, null, null,null);
            }

            if(address != null){
                ShowDiaDiemYeuThich();
                AnHien(viewthemdiadanh, 0, -500);
                AnHien(btnIcon, 0, -500);
                txtmapker0.setText("SEARCH");
                txtmapker1.setVisibility(View.GONE);
                txtmapker2.setText("Nằm ở: " + address.getAddressLine(0));
                txtmapker3.setText(vitritimkiem.latitude + "," + vitritimkiem.longitude);
                btnMaper1.setVisibility(View.VISIBLE);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(vitritimkiem, 10));

                for (DiaDiemYeuThich ddk : listDiaDiemKhac)
                {
                    if(ddk.getLatitude() == vitritimkiem.latitude && ddk.getLongitude() == vitritimkiem.longitude)
                    {
                        Glide.with(Main_Map.this).load(ddk.getAnhDiaDiem())
                                .into(avatarMaper);
                        txtmapker1.setText(ddk.getTenDiaDiem());
                        txtmapker0.setText("SEARCH");
                        btnMaper1.setText("THÊM VÀO");
                        diadiemsearch = ddk;
                        tam = 1;
                    }
                }


                for(DiaDiemYeuThich ddyt : listDiaDiemYeuThich)
                {
                    if(ddyt.getLatitude() == vitritimkiem.latitude && ddyt.getLongitude() == vitritimkiem.longitude)
                    {
                        Glide.with(Main_Map.this).load(ddyt.getAnhDiaDiem())
                                .into(avatarMaper);
                        txtmapker1.setVisibility(View.VISIBLE);
                        txtmapker1.setText(ddyt.getTenDiaDiem());
                        if(tuychon == 1) {
                            txtmapker0.setText("SEARCH");
                            btnMaper1.setText("ĐÃ THÊM");
                        }
                        if(tuychon == 2)
                        {
                            txtmapker0.setText("ĐỊA ĐIỂM \n YÊU THÍCH");
                            btnMaper1.setText("XÓA");
                        }
                        tam = 1;
                    }
                }

                if(tam == 0)
                {
                    Glide.with(Main_Map.this).load(R.drawable.anhdd)
                            .into(avatarMaper);
                    txtmapker0.setText("SEARCH");
                    btnMaper1.setText("THÊM VÀO");
                }

            }
        }catch (Exception ex){
            Toast.makeText(Main_Map.this, "Không Search Thấy Địa Điểm Này", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.isBuildingsEnabled();
        maso = Setmaso();

        // Gắn VT
        database.getReference().child("ViTri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nguoiDungMapAPI.clear();
                    map.clear();
                    sumnguoi = 0;
                    try {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String id = "";
                            for (NguoiDung ng : listNguoiDung) {
                                if (snapshot1.getKey().equals(ng.getMaSo()) && !snapshot1.getKey().equals(maso) && tuychon != 2) {
                                    if (!ng.getMaSo().equals(id)){
                                        nguoiDungMapAPI.add(ng);
                                        sumnguoi++;
                                        id = snapshot1.getKey();
                                    }
                                    ViTriGPS viTriGPSbanbe = snapshot1.getValue(ViTriGPS.class);
                                    vitribanbe = new LatLng(viTriGPSbanbe.getLatitude(), viTriGPSbanbe.longitude);
                                    GanViTriLenMap(2, vitribanbe, null, ng.getMaSo() + "-" + ng.getTen(),null);
                                    listView.setAdapter(nguoiDungMapAPI);

                                }
                                if (snapshot1.getKey().equals(ng.getMaSo()) && snapshot1.getKey().equals(maso)) {
                                    nguoiGui = ng;
                                    map.setOnMarkerClickListener(Main_Map.this);
                                    ViTriGPS viTriGPS = snapshot1.getValue(ViTriGPS.class);
                                    vitritoi = new LatLng(viTriGPS.getLatitude(), viTriGPS.getLongitude());
                                    addressList.clear();
                                    Geocoder geocoder = new Geocoder(Main_Map.this);
                                    try {
                                        addressList = geocoder.getFromLocation(vitritoi.latitude, vitritoi.longitude, 1);
                                    } catch (IOException e) {
                                    }
                                    Address address = addressList.get(0);
                                    if (address != null && start == 0) {
                                        avatarMaper = findViewById(R.id.viewAnhDaiDienMapker);
                                        Glide.with(Main_Map.this).load(ng.getAnhDaiDien())
                                                .placeholder(R.drawable.avatar)
                                                .into(avatarMaper);
                                        txtmapker0.setText("BẠN");
                                        txtmapker1.setText("ID: " + ng.getMaSo());
                                        txtmapker2.setText("Đang Đứng Tại: " + "\n" + address.getAddressLine(0));
                                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(vitritoi, 10));
                                        start = -1;
                                    }
                                    if (location != "")
                                        GanViTriLenMap(1, null, location, null,null);
                                    sdt = ng.getSoDienThoai();
                                    ImageView avatarpf = findViewById(R.id.imageProfile);
                                    Glide.with(Main_Map.this).load(ng.getAnhDaiDien())
                                            .placeholder(R.drawable.avatar)
                                            .into(avatarpf);
                                    TextView tenpf = findViewById(R.id.nameProfile);
                                    tenpf.setText(ng.getTen().toUpperCase());
                                    TextView sdtpf = findViewById(R.id.sdtProfile);
                                    sdtpf.setText(ng.getSoDienThoai());
                                    TextView gmailpf = findViewById(R.id.gmailProfile);
                                    gmailpf.setText(ng.getEmail());
                                    DiaDiemHienTai = address.getAddressLine(0);
                                }
                            }
                        }
                    }catch (Exception ex){}

                }
                /*mClusterManager.cluster();*/
                if(sumnguoi == 0)
                    textDS.setText("Danh Sách Trống");
                else
                    textDS.setText(sumnguoi + " Người Trực Tuyến");
                GanViTriLenMap(0,vitritoi,null,null,null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void KhaiBaoButtonList() {

        btnOke = findViewById(R.id.btnOKE);
        txtmapker0 = findViewById(R.id.txtMapker_0);
        txtmapker1 = findViewById(R.id.txtMapker_1);
        txtmapker2 = findViewById(R.id.txtMapker_2);
        txtmapker3 = findViewById(R.id.txtMapker_3);
        btnMaper1 = findViewById(R.id.btnMapker);
        btnIcon = findViewById(R.id.btnIcon);
        viewthemdiadanh = findViewById(R.id.layoutThemDiaDanh);
        avatarMaper = findViewById(R.id.viewAnhDaiDienMapker);
        txtTenDiaDanh = findViewById(R.id.txtTenDiaDanh);
        viewthongtin = findViewById(R.id.layoutMenu);
        btnshowthongtin = findViewById(R.id.btnThongTin);
        timkiemdiadiem = findViewById(R.id.sv_location);
        btntimkiemlist = findViewById(R.id.btnsv_list);
        timkiemlist = findViewById(R.id.txtSearhList);
        btnshowmap = findViewById(R.id.btnshowmap);
        textDS = findViewById(R.id.txtDSView);
        listView = findViewById(R.id.listND);
        this.listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        database = FirebaseDatabase.getInstance();
        mapview = findViewById(R.id.map);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void XuLyButton() {

        btnOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnHien(viewthemdiadanh,0,-500);
                AnHien(btnIcon,0,-500);
                if(chonAnh!= null) {
                    try {
                        btnMaper1.setVisibility(View.VISIBLE);
                        btnMaper1.setText("ĐỢI...");
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        Date date = new Date();
                        StorageReference reference = storage.getReference()
                                .child("DiaDiemYeuThich")
                                .child(date.getTime() + "");
                        reference.putFile(chonAnh.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String linkAnh = uri.toString();
                                            String tenDiaDiem = binding.txtTenDiaDanh.getText().toString();
                                            String[] toaDo = binding.txtMapker3.getText().toString().split(",");
                                            DiaDiemYeuThich diaDiemYeuThich = new DiaDiemYeuThich(tenDiaDiem, linkAnh, Double.parseDouble(toaDo[0]), Double.parseDouble(toaDo[1]));
                                            max = TimMaxDiaDiem();
                                            database.getReference()
                                                    .child("DiaDiemYeuThich")
                                                    .child("NguoiDung: " + maso)
                                                    .child(max + "")
                                                    .setValue(diaDiemYeuThich);
                                            txtTenDiaDanh.setText("");
                                            txtmapker1.setVisibility(View.VISIBLE);
                                            txtmapker1.setText(tenDiaDiem);
                                            btnMaper1.setText("ĐÃ THÊM");
                                            chonAnh = null;
                                        }
                                    });
                                }
                            }
                        });
                    }catch (Exception ex){btnMaper1.setText("THÊM THẤT BẠI");}
                }
                else
                {
                    DiaDiemYeuThich diaDiemYeuThich = null;
                    String linkAnh = "https://firebasestorage.googleapis.com/v0/b/messbcode-e084a.appspot.com/o/DiaDiemYeuThich%2F1638277355950?alt=media&token=ed75b59f-0c3f-4ac2-9362-b67a0e412306";
                    String tenDiaDiem = binding.txtTenDiaDanh.getText().toString();
                    String[] toaDo = binding.txtMapker3.getText().toString().split(",");
                    if(diadiemsearch.getLatitude() == Double.parseDouble(toaDo[0]) && diadiemsearch.getLongitude() == Double.parseDouble(toaDo[1]))
                    {
                        diaDiemYeuThich = new DiaDiemYeuThich(tenDiaDiem, diadiemsearch.getAnhDiaDiem(), Double.parseDouble(toaDo[0]), Double.parseDouble(toaDo[1]));
                    }
                    else
                    {
                       diaDiemYeuThich = new DiaDiemYeuThich(tenDiaDiem, linkAnh, Double.parseDouble(toaDo[0]), Double.parseDouble(toaDo[1]));
                    }

                    max = TimMaxDiaDiem();
                    database.getReference()
                            .child("DiaDiemYeuThich")
                            .child("NguoiDung: " + maso)
                            .child(max + "")
                            .setValue(diaDiemYeuThich);
                    txtTenDiaDanh.setText("");
                    txtmapker1.setVisibility(View.VISIBLE);
                    txtmapker1.setText(tenDiaDiem);
                    btnMaper1.setText("ĐÃ THÊM");
                }
            }
        });

        btnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 85);
            }



        });

        btnMaper1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnMaper1.getText().equals("THÊM VÀO"))
                {
                    if(btnIcon.getVisibility() == View.VISIBLE)
                    {
                        AnHien(viewthemdiadanh,0,-500);
                        AnHien(btnIcon,0,-500);
                    }
                    else{
                        AnHien(viewthemdiadanh,1,500);
                        AnHien(btnIcon,1,500);
                    }
                }

                if(btnMaper1.getText().equals("Chi Tiết"))
                {
                    try{
                        Intent intent0 = new Intent(Main_Map.this, ThongTinNguoiDung.class);
                        intent0.putExtra("masonguoixem", maso);
                        intent0.putExtra("maso", masonguoixem);
                        startActivity(intent0);
                    }catch (Exception ex){}
                }

                if(btnMaper1.getText().equals("XÓA"))
                {
                    btnMaper1.setText("ĐỢI...");
                    String[] toaDo = binding.txtMapker3.getText().toString().split(",");
                    for (DiaDiemYeuThich ddyt : listDiaDiemYeuThich) {
                        if(ddyt.getLatitude() == Double.parseDouble(toaDo[0]) && ddyt.getLongitude() == Double.parseDouble(toaDo[1]))
                        {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("DiaDiemYeuThich")
                                    .child("NguoiDung: " + maso)
                                    .child(ddyt.getMaDiaDiem())
                                    .setValue(null);
                            Glide.with(Main_Map.this).load(R.drawable.anhdd)
                                    .into(avatarMaper);
                            txtmapker1.setVisibility(View.VISIBLE);
                            txtmapker1.setText("");
                            btnMaper1.setText("THÊM VÀO");
                            if(tuychon == 2)
                                ShowDiaDiemYeuThich();
                        }
                    }
                }

            }
        });

        btnshowthongtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showthongtin)
                {
                    viewthongtin.animate().translationX(370);
                    showthongtin = false;
                    if(showmap)
                        AnHienListDS(0);
                }
                else
                {
                    viewthongtin.animate().translationX(-370);
                    showthongtin = true;
                }
                AnHien(viewthemdiadanh,0,-500);
                AnHien(btnIcon,0,-500);

            }
        });

        btnshowmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showmap == true)
                    AnHienListDS(0);
                else {
                    AnHienListDS(1);
                    if(!showthongtin)
                    {
                        viewthongtin.animate().translationX(-370);
                        showthongtin = true;
                        AnHien(viewthemdiadanh,0,-500);
                        AnHien(btnIcon,0,-500);
                    }
                }
            }
        });

        timkiemlist.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                TimKiemList();
                return false;
            }
        });

        btntimkiemlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timkiemlist.getText().toString().equals("")) {
                    if (showtimkiem == true) {
                        AnHien(timkiemlist, 1, 90);
                        AnHien(textDS, 0, -90);
                        showtimkiem = false;
                    } else {
                        AnHien(timkiemlist, 0, 0);
                        AnHien(textDS, 1, 0);
                        showtimkiem = true;
                    }
                }
                else
                {
                    TimKiemList();
                }
            }
        });

    }

    public void AnHienListDS(int anhien) {
        if(anhien == 0) {
            mapview.animate().translationY(0).withLayer();
            listView.setVisibility(View.INVISIBLE);
            btntimkiemlist.setVisibility(View.INVISIBLE);
            btnshowmap.animate().rotation(0);
            showmap = false;
            timkiemlist.setVisibility(View.INVISIBLE);
            AnHien(textDS, 0, 0);
        }else
        {
            textDS.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            btntimkiemlist.setVisibility(View.VISIBLE);
            mapview.animate().translationY(-410).withLayer();
            btnshowmap.animate().rotation(180);
            showmap = true;
        }
    }

    public void AnHien(View view,int anhien,int x){
        timkiemlist.setText("");
        if(anhien == 0) {
            view.animate().translationY(x);
            view.setVisibility(View.INVISIBLE);
        }
        else{
            view.setVisibility(View.VISIBLE);
            view.animate().translationY(x);
        }
    }



    private void CapQuyenGPS() {
        if(manager != null) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
                }else if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
                }else{
                    Toast.makeText(this,"Nhà cung cấp không cho phép",Toast.LENGTH_LONG).show();
                }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                CapQuyenGPS();
            }
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null)
            saveLocation(location);
        else
            Toast.makeText(this,"Không tìm thấy vị trí.",Toast.LENGTH_SHORT).show();
    }

    private void saveLocation(Location location) {
        ViTriGPS vitriGPS = new ViTriGPS(location.getLatitude(),location.getLongitude());
        database.getReference().child("ViTri").child(maso).setValue(vitriGPS);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        if(!marker.getTitle().equals("Search:"))
        {
            AnHien(viewthemdiadanh,0,-500);
            AnHien(btnIcon,0,-500);
        }

        if(marker.getTitle().equals("Bạn tại:")) {
            btnMaper1.setVisibility(View.INVISIBLE);
            txtmapker0.setText("BẠN");
            for (NguoiDung ng : listNguoiDung) {
                if(ng.getMaSo().equals(maso)) {
                    txtmapker1.setText("ID: " + maso);
                    addressList.clear();
                    avatarMaper = findViewById(R.id.viewAnhDaiDienMapker);
                    Glide.with(Main_Map.this).load(ng.getAnhDaiDien())
                            .placeholder(R.drawable.avatar)
                            .into(avatarMaper);
                    Geocoder geocoder = new Geocoder(Main_Map.this);
                    try {
                        addressList = geocoder.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude,1);
                    Address address = addressList.get(0);
                    if (address != null) {
                        txtmapker2.setText("Đang Đứng Tại: " + "\n" + address.getAddressLine(0));
                    }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else if(marker.getTitle().equals("Search:"))
        {    int tam = 0;
            addressList.clear();
            Geocoder geocoder = new Geocoder(Main_Map.this);
            try{
                addressList = geocoder.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude,1);
                Address address = addressList.get(0);
                if(address != null) {
                    for(DiaDiemYeuThich ddyt : listDiaDiemYeuThich) {
                        if(ddyt.getLatitude() == marker.getPosition().latitude && ddyt.getLongitude() == marker.getPosition().longitude) {
                            Glide.with(Main_Map.this).load(ddyt.getAnhDiaDiem())
                                    .into(avatarMaper);
                            txtmapker0.setText("SEARCH");
                            txtmapker1.setVisibility(View.VISIBLE);
                            txtmapker1.setText(ddyt.getTenDiaDiem());
                            txtmapker2.setText("Nằm ở: " + address.getAddressLine(0));
                            txtmapker3.setText(ddyt.getLatitude()+ "," + ddyt.getLongitude());
                            btnMaper1.setVisibility(View.VISIBLE);
                            btnMaper1.setText("ĐÃ THÊM");
                            tam = 1;
                        }
                    }

                if(tam == 0)
                {
                    Glide.with(Main_Map.this).load(R.drawable.anhdd)
                            .placeholder(R.drawable.anhdd)
                            .into(avatarMaper);
                    txtmapker0.setText("SEARCH");
                    txtmapker1.setVisibility(View.GONE);
                    txtmapker2.setText("Nằm ở: " + address.getAddressLine(0));
                    txtmapker3.setText(marker.getPosition().latitude + "," + marker.getPosition().longitude);
                    btnMaper1.setVisibility(View.VISIBLE);
                    btnMaper1.setText("THÊM VÀO");
                }

                }
            }catch (Exception e) {
            }
        }

        else if(marker.getTitle().equals("Địa điểm yêu thích:"))
        {

            for(DiaDiemYeuThich ddyt : listDiaDiemYeuThich)
            {
                if(SoSanhToaDo(ddyt.getLatitude(),marker.getPosition().latitude) == marker.getPosition().latitude  && SoSanhToaDo(ddyt.getLongitude(),marker.getPosition().longitude) == marker.getPosition().longitude)
                {
                    addressList.clear();
                    Geocoder geocoder = new Geocoder(Main_Map.this);
                    try{
                        addressList = geocoder.getFromLocation(ddyt.getLatitude(),ddyt.getLongitude(),1);
                        Address address = addressList.get(0);
                        if(address != null) {
                            Glide.with(Main_Map.this).load(ddyt.getAnhDiaDiem())
                                    .placeholder(R.drawable.anhdd)
                                    .into(avatarMaper);
                            txtmapker0.setText("ĐỊA ĐIỂM \n YÊU THÍCH");
                            txtmapker1.setVisibility(View.VISIBLE);
                            txtmapker1.setText(ddyt.getTenDiaDiem());
                            txtmapker2.setText("Nằm ở: " + address.getAddressLine(0));
                            txtmapker3.setText(ddyt.getLatitude() + "," + ddyt.getLongitude());
                            btnMaper1.setVisibility(View.VISIBLE);
                            btnMaper1.setText("XÓA");
                        }
                    }catch (Exception e) {
                    }
                }
            }
        }
        else
        {
            String[] ma = marker.getTitle().split("-");
            for (NguoiDung ng : listNguoiDung) {
                if(ng.getMaSo().equals(ma[0])) {
                    txtmapker0.setText(ng.getTen());
                    txtmapker1.setText("ID: " + ng.getMaSo());
                    masonguoixem = ng.getMaSo();
                    addressList.clear();
                    avatarMaper = findViewById(R.id.viewAnhDaiDienMapker);
                    Glide.with(Main_Map.this).load(ng.getAnhDaiDien())
                            .placeholder(R.drawable.avatar)
                            .into(avatarMaper);
                    Geocoder geocoder = new Geocoder(Main_Map.this);
                    try {
                        addressList = geocoder.getFromLocation(ng.getLatitude(), ng.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    if (address != null) {
                        txtmapker2.setText("Đang Đứng Tại: " + "\n" + address.getAddressLine(0));
                        btnMaper1.setVisibility(View.VISIBLE);
                        btnMaper1.setText("Chi Tiết");
                    }
                }
            }
        }


        /*         database.getReference().child("ViTri").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for(DataSnapshot snapshot1 : snapshot.getChildren())
                           if(snapshot1.getKey() == "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/

        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        maso = Setmaso();
        String maHoatDong ="NguoiDung " + maso;
        database.getReference().child("TrangThaiHoatDong").child(maHoatDong).setValue("Ngoại tuyến");

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 85) {
            if (data != null) {
                if (data.getData() != null) {
                    binding.viewAnhDaiDienMapker.setImageURI(data.getData());
                    chonAnh = data;
                }
            }
        }
    }


}