package com.doanjava.messbcode.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.doanjava.messbcode.databinding.ActivityDoiMatKhauThanhCongBinding;

public class DoiMatKhauThanhCong extends AppCompatActivity {

    ActivityDoiMatKhauThanhCongBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoiMatKhauThanhCongBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String maso = getIntent().getStringExtra("maso");

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoiMatKhauThanhCong.this, KichHoatSoDienThoai.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
                finishAffinity();
            }
        });
    }
}