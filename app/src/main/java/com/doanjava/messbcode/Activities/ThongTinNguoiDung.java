package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Adapters.ChuyenDoiSlider;
import com.doanjava.messbcode.Models.DiaDiemYeuThich;
import com.doanjava.messbcode.Models.LuotYeuThich;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.ViTriGPS;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityThongTinNguoiDungBinding;
import com.doanjava.messbcode.databinding.ActivityWelcomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.util.ArrayList;

public class ThongTinNguoiDung extends AppCompatActivity {


    ActivityThongTinNguoiDungBinding binding;
    FirebaseDatabase database;
    int soluotyeuthich,dembanbe = 0;
    LuotYeuThich dsyeuthich = null;
    String masonguoixem;
    String maso;
    NguoiDung nguoiDung;
    boolean checktym = false;
    int dembanchung = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThongTinNguoiDungBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        masonguoixem = getIntent().getStringExtra("masonguoixem");
        maso = getIntent().getStringExtra("maso");
        getSupportActionBar().show();
        getSupportActionBar().setTitle("THÔNG TIN");
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
        //Lấy Lượt Yêu Thích

        database.getReference().child("LuotYeuThich").child("NguoiDung: " + maso).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                soluotyeuthich = 0;
                dsyeuthich = null;
                if(snapshot.exists()) {
                    dsyeuthich = snapshot.getValue(LuotYeuThich.class);
                    if (dsyeuthich != null) {
                        soluotyeuthich = dsyeuthich.getNguoiYeuThich().size();
                        if (dsyeuthich.getNguoiYeuThich().contains(masonguoixem)) {
                            checktym = true;
                            binding.imgtym.setColorFilter(Color.RED);
                        } else {
                            checktym = false;
                            binding.imgtym.setColorFilter(Color.WHITE);
                        }
                    }
                }
                else
                {
                    checktym = false;
                    binding.imgtym.setColorFilter(Color.WHITE);
                }
                binding.yeuthich.setText(soluotyeuthich + "");

                binding.imgtym.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> dsmoi = new ArrayList<>();
                        LuotYeuThich luotYeuThich = new LuotYeuThich();
                        if(checktym) {
                            if(dsyeuthich!=null)
                            for(String ma : dsyeuthich.getNguoiYeuThich())
                                if(!ma.equals(masonguoixem))
                                    dsmoi.add(ma);

                        }
                        else {
                            if(dsyeuthich != null)
                                dsmoi = dsyeuthich.getNguoiYeuThich();
                            else
                                dsmoi = new ArrayList<>();
                            dsmoi.add(masonguoixem);
                        }
                        luotYeuThich.setNguoiYeuThich(dsmoi);
                        database.getReference()
                                .child("LuotYeuThich")
                                .child("NguoiDung: " + maso)
                                .setValue(luotYeuThich);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Lay DS BAN BE
        database.getReference().child("TroChuyen").child(maso).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dembanbe = 0;
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getChildrenCount() > 0) {
                        dembanbe++;
                        if (snapshot1.getKey().equals(masonguoixem))
                            binding.imgbanbe.setColorFilter(Color.BLUE);
                    }

                    binding.banbe.setText(dembanbe + "");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //LAY DS BAN CHUNG
        database.getReference().child("TroChuyen").child(maso).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dembanchung = 0;
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    database.getReference().child("TroChuyen").child(masonguoixem).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snapshot2 : snapshot.getChildren()) {
                                if (snapshot1.getChildrenCount() > 0 && snapshot2.getChildrenCount() > 0)
                                    if(snapshot1.getKey().equals(snapshot2.getKey()) && !snapshot1.getKey().equals(masonguoixem) && !snapshot2.getKey().equals(maso))
                                        dembanchung++;
                                binding.banchung.setText("Có " + dembanchung + " Bạn Chung");
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

        //Lay DS Nguoi Dung
        database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    try {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            database.getReference().child("ViTri").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                        if (snapshot2.getKey().equals(snapshot1.getKey())) {
                                            ArrayList<Address> addressList = new ArrayList<>();
                                            nguoiDung = snapshot1.getValue(NguoiDung.class);
                                            if (nguoiDung.getMaSo().equals(maso)) {
                                                ViTriGPS vitri = snapshot2.getValue(ViTriGPS.class);
                                                nguoiDung.setLatitude(vitri.latitude);
                                                nguoiDung.setLongitude(vitri.longitude);
                                                binding.name.setText(nguoiDung.getTen().toUpperCase() + "");
                                                binding.ngaysinh.setText(nguoiDung.getNgaySinh() + "");
                                                binding.sdt.setText(nguoiDung.getSoDienThoai() + "");
                                                binding.gioitinh.setText(nguoiDung.getGioiTinh() + "");
                                                Geocoder geocoder = new Geocoder(ThongTinNguoiDung.this);
                                                Glide.with(ThongTinNguoiDung.this)
                                                        .load(nguoiDung.getAnhDaiDien())
                                                        .into(binding.profile);
                                                try {
                                                    addressList = (ArrayList<Address>) geocoder.getFromLocation(nguoiDung.getLatitude(), nguoiDung.getLongitude(), 1);
                                                    Address address = addressList.get(0);
                                                    if (address != null) {
                                                        binding.vitri.setText(address.getFeatureName() + "- " + address.getAdminArea() + "- " + address.getCountryName());
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    } catch (Exception ex) { }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}