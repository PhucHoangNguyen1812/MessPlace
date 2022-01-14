package com.doanjava.messbcode.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Network.ApiClient;
import com.doanjava.messbcode.Network.ApiService;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityCallVideoBinding;
import com.doanjava.messbcode.databinding.ActivityNhanTinBinding;
import com.doanjava.messbcode.utilities.Constants;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Call_Video extends AppCompatActivity {
    ActivityCallVideoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        String ten = getIntent().getStringExtra(Constants.KEY_NAME);
        String anh = getIntent().getStringExtra(Constants.KEY_ANH);
        if(ten != null) {
            binding.callName.setText(ten);
        }
        if(anh != null)
        {
            Glide.with(Call_Video.this).load(anh)
                    .placeholder(R.drawable.avatar)
                    .into(binding.avatarcall);
        }
        binding.checkTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(
                        Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
            }
        });
        binding.checkFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(
                        Constants.REMOTE_MSG_INVITATION_REJECTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
                finish();
            }
        });


        CountDownTimer dem = new CountDownTimer(20000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if(binding.vongTrong.getVisibility() == View.INVISIBLE && binding.vongNgoai.getVisibility() == View.INVISIBLE )
                {
                    binding.vongTrong.setVisibility(View.VISIBLE);
                    binding.tinhtrang.setText("Gọi Đến.");
                }
                else if(binding.vongTrong.getVisibility() == View.VISIBLE && binding.vongNgoai.getVisibility() == View.INVISIBLE)
                {
                    binding.vongNgoai.setVisibility(View.VISIBLE);
                    binding.tinhtrang.setText("Gọi Đến..");
                }
                else
                {
                    binding.vongTrong.setVisibility(View.INVISIBLE);
                    binding.vongNgoai.setVisibility(View.INVISIBLE);
                    binding.tinhtrang.setText("Gọi Đến...");

                }

            }

            @Override
            public void onFinish() {
            }
        };

        dem.start();



    }

    private void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);


            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

            sentRemoteMessage(body.toString(),type);
        } catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void sentRemoteMessage(String remoteMesssageBody, String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(),remoteMesssageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if(response.isSuccessful()) {
                    if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {

                        try {

                                URL serverURL = new URL("https://meet.jit.si");
                                JitsiMeetConferenceOptions defaultOptions =
                                        new JitsiMeetConferenceOptions.Builder()
                                                .setServerURL(serverURL)
                                                .setWelcomePageEnabled(false)
                                                .setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_CALL_ROOM))
                                                .build();

                                JitsiMeetActivity.launch(Call_Video.this,defaultOptions);
                                finish();

                        }catch (Exception ex){
                            Toast.makeText(Call_Video.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }else
                    {
                        Toast.makeText(Call_Video.this, "Tu Choi", Toast.LENGTH_SHORT).show();
                    }

                } else{
                        Toast.makeText(Call_Video.this,response.message(),Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull  Call<String> call,@NonNull Throwable t) {

                Toast.makeText(Call_Video.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null)
            {
                if(type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED))
                {
                    Toast.makeText(context, "Hủy", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}