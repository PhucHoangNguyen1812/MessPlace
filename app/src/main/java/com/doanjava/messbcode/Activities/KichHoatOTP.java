
package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.doanjava.messbcode.databinding.ActivityKichHoatOTPBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class KichHoatOTP extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityKichHoatOTPBinding binding;
    FirebaseAuth auth;
    String verificationId;
    static final String TAG = "MAIN_TAG";
    PhoneAuthProvider.ForceResendingToken forceResendingToken;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKichHoatOTPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang Gửi Mã OTP...");
        dialog.setCancelable(false);
        dialog.show();

        auth = FirebaseAuth.getInstance();
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String maso = getIntent().getStringExtra("maso");

        getSupportActionBar().hide();


        binding.phoneLbl.setText(phoneNumber);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(KichHoatOTP.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(KichHoatOTP.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        Log.d(TAG ,"onCodeSent: " + verifyId);
                        verificationId = verifyId;
                        forceResendingToken = token;
                        dialog.dismiss();
                        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        binding.otpView.requestFocus();

                        Toast.makeText(KichHoatOTP.this, "Mã Xác Minh Đã Gửi...", Toast.LENGTH_SHORT).show();
                    }

                }).build();


        PhoneAuthProvider.verifyPhoneNumber(options);



        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(KichHoatOTP.this, "Xác Nhận OTP Thành Công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(KichHoatOTP.this, ThongTinTaiKhoan.class));
                                finish();
                        } else {
                            Toast.makeText(KichHoatOTP.this, " Lỗi Xác Nhận OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KichHoatOTP.this, KichHoatSoDienThoai.class);
                intent.putExtra("phoneNumber",phoneNumber.toString().substring(3));
                intent.putExtra("maso",maso);
                startActivity(intent);
            }
        });
    }
}