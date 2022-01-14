package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.doanjava.messbcode.databinding.ActivityQuenMatKhauBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuenMatKhau extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityQuenMatKhauBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuenMatKhauBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        getSupportActionBar().hide();
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String maso = getIntent().getStringExtra("maso");


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuenMatKhau.this, KiemTraEmail.class);
                intent.putExtra("phoneNumber",phoneNumber);
                intent.putExtra("maso",maso);
                startActivity(intent);
            }
        });



        binding.countinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matkhau = binding.matkhau.getText().toString();
                if (matkhau.isEmpty()) {
                    binding.matkhau.setError("Vui lòng điền Mật Khẩu Mới.");
                    return;
                } else if (matkhau.length() < 6) {
                    binding.matkhau.setError(("Mật khẩu mới phải hơn 6 kí tự."));
                    return;
                }

                String nhaplaimatkhau = binding.xacnhanmatkhau.getText().toString();
                if (nhaplaimatkhau.isEmpty()) {
                    binding.xacnhanmatkhau.setError("Vui lòng điền Xác thực mật khẩu mới.");
                    return;
                }

                if (!nhaplaimatkhau.equals(matkhau)) {
                    binding.xacnhanmatkhau.setError("Mật khẩu mới không khớp.");
                    return;
                }

                database.getReference().child("NguoiDung").child(maso).child("matKhau").setValue(matkhau).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(QuenMatKhau.this, "Cập Nhập Mật Khẩu Mới Thành Công.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(QuenMatKhau.this, DoiMatKhauThanhCong.class);
                        intent.putExtra("phoneNumber",phoneNumber);
                        startActivity(intent);
                        finishAffinity();
                    }
                });

            }

        });



    }
}