package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.databinding.ActivityKichHoatSoDienThoaiBinding;



import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.doanjava.messbcode.R;


public class KichHoatSoDienThoai extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;


    String maso = "";
    String matkhau = "";
    String phoneNumber;
    FirebaseDatabase database;
    ActivityKichHoatSoDienThoaiBinding binding;
    FirebaseAuth auth;
    boolean checkdangnhap = false;
    boolean checkdoimatkhau = false;
    private GoogleSignInClient mGoogleSignInClient;


    public String Kiemtra()
    {
        String phoneNumber = binding.phoneBox.getText().toString().trim();
        if(phoneNumber.isEmpty()){
            binding.phoneBox.setError("Vui lòng nhập số điện thoại");
            return "";
        }
        if (phoneNumber.length() < 9 || phoneNumber.length() > 12) {
            binding.phoneBox.setError("Nhập số điện thoại hợp lệ");
            return "";
        }
        else if (phoneNumber.indexOf("0") == 0 || phoneNumber.indexOf('-') == 0)
            phoneNumber = phoneNumber.substring(1,10);
        else if(phoneNumber.indexOf("84") == 0)
            phoneNumber = phoneNumber.substring(2,11);

        return "+84" + phoneNumber;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKichHoatSoDienThoaiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestGoogleSignIn();
        database = FirebaseDatabase.getInstance();
        getSupportActionBar().hide();
        binding.phoneBox.requestFocus();
        auth = FirebaseAuth.getInstance();

        binding.loginGg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        /* binding.loginFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbackManager = CallbackManager.Factory.create();

                LoginManager manager = LoginManager.getInstance();

                manager.logInWithReadPermissions(KichHoatSoDienThoai.this, Arrays.asList("public_profile","email"));
                manager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {


                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
            }
        }); */

        String sdt = getIntent().getStringExtra("phoneNumber");
        if(sdt != null)
        {
            binding.phoneBox.setText(sdt);
            binding.phoneBox.setSelection(sdt.length());
        }

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = Kiemtra();
                if(phoneNumber == "")
                    return;
                database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int tontai = 0;
                        phoneNumber = Kiemtra();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            NguoiDung nguoiDung = snapshot1.getValue(NguoiDung.class);
                            if(nguoiDung.getSoDienThoai().equals(phoneNumber)) {
                                tontai++;
                            }

                        }
                        if (tontai != 0) {
                            binding.phoneBox.setError("Số điện thoại đã đăng kí.");
                            return;
                        }
                        else
                        {
                            Intent intent = new Intent(KichHoatSoDienThoai.this,KichHoatOTP.class);
                            intent.putExtra("phoneNumber",phoneNumber);
                            intent.putExtra("maso", maso);
                            startActivity(intent);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

        });
        binding.dangnhapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkdangnhap = true;
                phoneNumber = Kiemtra();
                database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
                    int tontai = 0;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String phoneNumber = Kiemtra();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            NguoiDung nguoiDung = snapshot1.getValue(NguoiDung.class);
                            if (nguoiDung.getSoDienThoai().equals(phoneNumber)) {
                                tontai++;
                                maso = snapshot1.getKey();
                                matkhau = nguoiDung.getMatKhau();
                            }}
                        if (tontai == 0) {
                            binding.phoneBox.setError("Số điện thoại chưa đăng ký. Vui lòng nhấn vào Đăng ký.");
                            return;
                        }else {
                            if (binding.Password.getText().toString().isEmpty()) {
                                binding.Password.setError("Vui lòng điền mật khẩu.");
                                return;
                            }
                            if(!matkhau.equals(binding.Password.getText().toString().trim())) {
                                binding.Password.setError("Mật Khẩu Sai.");
                                return;
                            }
                        }

                        if(checkdangnhap == true && matkhau.equals(binding.Password.getText().toString().trim()) && !matkhau.equals("")){
                            checkdangnhap = false;
                            Intent intent = new Intent(KichHoatSoDienThoai.this, Main_Map.class);
                            intent.putExtra("phoneNumber", phoneNumber);
                            intent.putExtra("maso", maso);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });

        binding.btnQuenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkdoimatkhau = true;
                phoneNumber = Kiemtra();
                if(phoneNumber == "")
                    return;
                else {
                    database.getReference().child("NguoiDung").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int tontai = 0;
                            phoneNumber = Kiemtra();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                NguoiDung nguoiDung = snapshot1.getValue(NguoiDung.class);
                                if(nguoiDung.getSoDienThoai().equals(phoneNumber)) {
                                    tontai++;
                                    maso = snapshot1.getKey();
                                }

                            }
                            if (tontai == 0) {
                                binding.phoneBox.setError("Số điện thoại chưa đăng ký. Vui lòng nhấn vào Đăng ký.");
                                return;
                            }
                            else if(checkdoimatkhau == true)
                            {
                                checkdoimatkhau = false;
                                Intent intent = new Intent(KichHoatSoDienThoai.this, KiemTraEmail.class);
                                intent.putExtra("phoneNumber",phoneNumber);
                                intent.putExtra("maso",maso);
                                startActivity(intent);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }



            }
        });

        binding.returnwelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KichHoatSoDienThoai.this, Welcome.class);
                startActivity(intent);
            }
        });
    }


    private void requestGoogleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }


    /*private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(KichHoatSoDienThoai.this, "Success.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(KichHoatSoDienThoai.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(KichHoatSoDienThoai.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    } */


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            Intent intent = new Intent(KichHoatSoDienThoai.this, MainActivity.class);
                            startActivity(intent);
                        } else {

                            Toast.makeText(KichHoatSoDienThoai.this, "Lỗi Đăng Nhập Google", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating user with firebase using received token id
                firebaseAuthWithGoogle(account.getIdToken());

                //assigning user information to variables
                String userName = account.getDisplayName();
                String userEmail = account.getEmail();
                String userPhoto = account.getPhotoUrl().toString();
                userPhoto = userPhoto+"?type=large";

                //create sharedPreference to store user data when user signs in successfully
                SharedPreferences.Editor editor = getApplicationContext()
                        .getSharedPreferences("MyPrefs",MODE_PRIVATE)
                        .edit();
                editor.putString("username", userName);
                editor.putString("useremail", userEmail);
                editor.putString("userPhoto", userPhoto);
                editor.apply();

                Log.i(TAG, "onActivityResult: Success");

            } catch (ApiException e) {
                Log.e(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = auth.getCurrentUser();
//        if (user != null){
//            startActivity(new Intent(getApplicationContext(), Main_Map.class));
//        }
//    }

}