package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.databinding.ActivityThongTinTaiKhoanBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.doanjava.messbcode.Models.NguoiDung;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ThongTinTaiKhoan extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private Uri chonAnh;
    private ActivityThongTinTaiKhoanBinding binding;
    long stt = 0;
    NguoiDung nguoiDung = new NguoiDung();
    public String ChuyenMaSo(long stt)
    {
        stt += 1;
        if(stt < 10)
            return "0" + stt;
        return ""+ stt;
    }



    public int KiemTraNgaySinh(int ngay, int thang, int nam)
    {
        int i = 0;
        switch (thang) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (ngay <= 0 || ngay > 31){
                    i ++;
                    binding.ngaytxt.setError("Tháng " + thang + " không có ngày " + ngay);
                }

                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (thang <= 0 || thang > 30) {
                    i ++;
                    binding.ngaytxt.setError("Tháng " + thang + " không có ngày " + ngay);
                }
                break;
            case 2:
                boolean check = false;
                if(nam % 4 == 0)
                {
                    if( nam % 100 == 0)
                    {
                        if ( nam % 400 == 0)
                            check = true;
                        else
                            check = false;
                    }
                    else
                        check = true;
                }
                else
                    check = false;


                if (ngay <= 0 || ngay > 29) {
                    i ++;
                    binding.ngaytxt.setError("Tháng " + thang + " không có ngày " + ngay);
                }else if (!check && ngay == 29){
                    i ++;
                    binding.ngaytxt.setError("Tháng " + thang + " trong Năm " + nam + " không có ngày " + ngay);
                }
                break;

            default:
                i ++;
                binding.thangtxt.setError("Một năm chỉ có 12 tháng");
                break;
        }
        return i;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThongTinTaiKhoanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang Cập Nhật Thông Tin...");
        dialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();

        nguoiDung = (NguoiDung) getIntent().getSerializableExtra("nguoiDung");

        if(nguoiDung!=null)
        {
            binding.dangkyBtn.setText("CẬP NHẬP");
            Glide.with(ThongTinTaiKhoan.this)
                    .load(nguoiDung.getAnhDaiDien())
                    .into(binding.AnhDD);
            binding.tenBox.setText(nguoiDung.getTen());
            binding.emailBox.setText(nguoiDung.getEmail());
            String[] ngaysinh = nguoiDung.getNgaySinh().split("/");
            binding.txtMatKhau.setHint("Mật Khẩu Mới");
            binding.txtXacNhanMatKhau.setHint("Nhập Lại Mật Khẩu Mới");
            binding.ngaytxt.setText(ngaysinh[0]);
            binding.thangtxt.setText(ngaysinh[1]);
            binding.namtxt.setText(ngaysinh[2]);
            if(nguoiDung.getGioiTinh().equals("Nam"))
                binding.namRbtn.setChecked(true);
            else
                binding.nuRbtn.setChecked(true);
        }

        database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stt = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.AnhDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });

        binding.tenBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int length = binding.tenBox.getText().length();
                if(length == 25 && event.getKeyCode() != KeyEvent.KEYCODE_DEL)
                    return true;
                return false;
            }
        });

        binding.emailBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int length = binding.tenBox.getText().length();
                if(length == 30 && event.getKeyCode() != KeyEvent.KEYCODE_DEL)
                    return true;
                return false;
            }
        });

        binding.ngaytxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int length = binding.ngaytxt.getText().length();
                if(length == 2 && event.getKeyCode() != KeyEvent.KEYCODE_DEL)
                    return true;
                return false;
            }
        });

        binding.thangtxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int length = binding.thangtxt.getText().length();
                if(length == 2 && event.getKeyCode() != KeyEvent.KEYCODE_DEL)
                    return true;
                return false;
            }
        });

        binding.namtxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int length = binding.namtxt.getText().length();
                if(length == 4 && event.getKeyCode() != KeyEvent.KEYCODE_DEL)
                    return true;
                return false;
            }
        });



        binding.dangkyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ten = binding.tenBox.getText().toString();
                if (ten.isEmpty()) {
                    binding.tenBox.setError("Vui lòng điền Tên.");
                    return;
                }

                String email = binding.emailBox.getText().toString();
                if (email.isEmpty()) {
                    binding.emailBox.setError("Vui lòng điền Email.");
                    return;
                } else if (email.lastIndexOf("@gmail.com") == -1) {
                    binding.emailBox.setError("Email không hợp lệ.");
                    return;
                } else if(email.length() < 16)
                {
                    binding.emailBox.setError("Tên email phải > 6 kí tự.");
                    return;
                }

                String matkhau = binding.matkhauBox.getText().toString();
                if (matkhau.isEmpty()) {
                    if(nguoiDung == null) {
                        binding.matkhauBox.setError("Vui lòng điền Mật Khẩu.");
                        return;
                    }
                } else if (matkhau.length() < 6) {
                    binding.matkhauBox.setError(("Mật khẩu phải hơn 6 kí tự."));
                    return;
                }

                String nhaplaimatkhau = binding.nhaplaimatkhauBox.getText().toString();
                if (nhaplaimatkhau.isEmpty()) {
                    if(nguoiDung == null) {
                        binding.nhaplaimatkhauBox.setError("Vui lòng điền Xác thực mật khẩu.");
                        return;
                    }
                }

                if (!nhaplaimatkhau.equals(matkhau)) {
                    binding.nhaplaimatkhauBox.setError("Mật khẩu không khớp.");
                    return;
                }

                String ngay = binding.ngaytxt.getText().toString();
                String thang = binding.thangtxt.getText().toString();
                String nam = binding.namtxt.getText().toString();

                if (nam.isEmpty()) {
                    binding.namtxt.setError("Vui lòng điền đầy đủ ngày sinh.");
                    return;
                }else if(thang.isEmpty()){
                    binding.thangtxt.setError("Vui lòng điền tháng.");
                    return;
                }else if(ngay.isEmpty()){
                    binding.ngaytxt.setError("Vui lòng điền ngày.");
                    return;
                }


                else if(Integer.parseInt(nam) < 1800 || Integer.parseInt(nam) > 2021)
                {
                    binding.namtxt.setError("Bạn có chắc đây là ngày sinh của bạn.");
                    return;
                }

                if(KiemTraNgaySinh(Integer.parseInt(ngay),Integer.parseInt(thang),Integer.parseInt(nam)) != 0)
                    return;
                dialog.show();
                if (chonAnh != null) {
                    StorageReference reference = storage.getReference().child("Files").child("Hinh");
                    reference.putFile(chonAnh).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String maSoND = ChuyenMaSo(stt);
                                        String anhDaiDien = uri.toString();
                                        String email = binding.emailBox.getText().toString();
                                        String ngaySinh = binding.ngaytxt.getText().toString() + "/" + binding.thangtxt.getText().toString() + "/" + binding.namtxt.getText().toString();
                                        String ten = binding.tenBox.getText().toString();
                                        String matkhau = binding.matkhauBox.getText().toString();
                                        String gioiTinh = "";
                                        if (binding.namRbtn.isChecked() == true)
                                            gioiTinh = "Nam";
                                        else
                                            gioiTinh = "Nữ";

                                        if (nguoiDung == null) {
                                            String soDienThoai = auth.getCurrentUser().getPhoneNumber();
                                            NguoiDung nguoidungmoi = new NguoiDung(maSoND, ten, email, matkhau, gioiTinh, anhDaiDien, ngaySinh, soDienThoai, 0, 0);
                                            database.getReference()
                                                    .child("NguoiDung")
                                                    .child(maSoND)
                                                    .setValue(nguoidungmoi)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            dialog.dismiss();
                                                            FirebaseMessaging.getInstance()
                                                                    .getToken()
                                                                    .addOnSuccessListener(new OnSuccessListener<String>() {
                                                                        @Override
                                                                        public void onSuccess(String token) {
                                                                            HashMap<String,Object> map = new HashMap<>();
                                                                            map.put("token",token);
                                                                            database.getReference()
                                                                                    .child("NguoiDung")
                                                                                    .child(maSoND)
                                                                                    .updateChildren(map);

                                                                        }
                                                                    });
                                                            Intent intent = new Intent(ThongTinTaiKhoan.this, Main_Map.class);
                                                            intent.putExtra("maso", maSoND);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                        }
                                        else
                                        {
                                            HashMap<String, Object> capnhap = new HashMap<>();
                                            if(!matkhau.isEmpty())
                                            {
                                                capnhap.put("matKhau",matkhau);

                                            }
                                            capnhap.put("anhDaiDien",anhDaiDien);
                                            capnhap.put("email",email);
                                            capnhap.put("gioiTinh",gioiTinh);
                                            capnhap.put("ngaySinh",ngaySinh);
                                            capnhap.put("ten",ten);

                                            database.getReference()
                                                    .child("NguoiDung")
                                                    .child(nguoiDung.getMaSo())
                                                    .updateChildren(capnhap);

                                            dialog.dismiss();
                                            Toast.makeText(ThongTinTaiKhoan.this,"CẬP NHẬP THÀNH CÔNG",Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                            }

                        }
                    });
                } else {
                    String maSoND = ChuyenMaSo(stt);
                    String ngaySinh = binding.ngaytxt.getText().toString() + "/" + binding.thangtxt.getText().toString() + "/" + binding.namtxt.getText().toString();
                    String gioiTinh = "";
                    if (binding.namRbtn.isChecked() == true)
                        gioiTinh = "Nam";
                    else
                        gioiTinh = "Nữ";

                    if(nguoiDung == null) {
                        String soDienThoai = auth.getCurrentUser().getPhoneNumber();
                        NguoiDung nguoidung = new NguoiDung(maSoND, ten, email, matkhau, gioiTinh, "Không Ảnh", ngaySinh, soDienThoai, 0, 0);

                        database.getReference()
                                .child("NguoiDung")
                                .child(maSoND)
                                .setValue(nguoidung)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                        FirebaseMessaging.getInstance()
                                                .getToken()
                                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(String token) {
                                                        HashMap<String,Object> map = new HashMap<>();
                                                        map.put("token",token);
                                                        database.getReference()
                                                                .child("NguoiDung")
                                                                .child(maSoND)
                                                                .updateChildren(map);

                                                    }
                                                });
                                        Intent intent = new Intent(ThongTinTaiKhoan.this, Main_Map.class);
                                        intent.putExtra("maso", maSoND);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                    }
                    else
                    {
                        HashMap<String, Object> capnhap = new HashMap<>();
                        if(!matkhau.isEmpty())
                        {
                            capnhap.put("matKhau",matkhau);

                        }
                        capnhap.put("email",email);
                        capnhap.put("gioiTinh",gioiTinh);
                        capnhap.put("ngaySinh",ngaySinh);
                        capnhap.put("ten",ten);

                        database.getReference()
                                .child("NguoiDung")
                                .child(nguoiDung.getMaSo())
                                .updateChildren(capnhap);

                        dialog.dismiss();
                        Toast.makeText(ThongTinTaiKhoan.this,"CẬP NHẬP THÀNH CÔNG",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {
                binding.AnhDD.setImageURI(data.getData());
                chonAnh = data.getData();
            }
        }
    }


}
