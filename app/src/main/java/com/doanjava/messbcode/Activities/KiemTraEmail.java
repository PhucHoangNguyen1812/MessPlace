package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.doanjava.messbcode.databinding.ActivityKiemTraEmailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class KiemTraEmail extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityKiemTraEmailBinding binding;
    public static String tam =  "";
    public String TraSao(int length)
    {
        String sao = "";
        for(int i = 0 ; i< length ; i ++)
            sao += '*';
        return sao;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKiemTraEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        getSupportActionBar().hide();
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String maso = getIntent().getStringExtra("maso");
        database.getReference().child("NguoiDung").child(maso).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String emailtam  = snapshot.getValue().toString();
                binding.emailBox.setText("Email: " + emailtam.substring(0,2) + TraSao(emailtam.length() - 14) + emailtam.substring((emailtam.length() - 12), emailtam.length()));
                tam = emailtam;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        binding.email.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int length = tam.length();
                if(binding.email.getText().toString().compareTo(tam.toString()) == 0 )
                {

                    tam = "";
                    Intent intent = new Intent(KiemTraEmail.this, QuenMatKhau.class);
                    intent.putExtra("phoneNumber",phoneNumber.toString().substring(3));
                    intent.putExtra("maso",maso);
                    startActivity(intent);

                }

                else if(binding.email.length() == length && event.getKeyCode() != KeyEvent.KEYCODE_DEL)
                {
                    binding.email.setError("Xác nhận không khớp.");
                    return true;
                }

                return false;
            }
        });



        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KiemTraEmail.this, KichHoatSoDienThoai.class);
                intent.putExtra("phoneNumber",phoneNumber.toString().substring(3));
                intent.putExtra("maso",maso);
                startActivity(intent);
            }
        });

    }
}