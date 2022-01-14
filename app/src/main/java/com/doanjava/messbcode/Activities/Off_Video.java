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
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Network.ApiClient;
import com.doanjava.messbcode.Network.ApiService;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityOffVideoBinding;
import com.doanjava.messbcode.utilities.Constants;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Off_Video extends AppCompatActivity {


    private String inviterToken = null;
    NguoiDung nguoiNhan = new NguoiDung();
    NguoiDung nguoiGui = new NguoiDung();
    String meetingRoom = null;
    ActivityOffVideoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOffVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        //preferenceManager = new PreferenceManager(getApplicationContext());
        nguoiNhan = (NguoiDung) getIntent().getSerializableExtra("nguoinhan");
        nguoiGui = (NguoiDung) getIntent().getSerializableExtra("nguoigui");
        //Toast.makeText(Off_Video.this,nguoiGui.getTen() + " ",Toast.LENGTH_SHORT).show();
        String meetingType = getIntent().getStringExtra("type");
        binding.callName.setText(nguoiNhan.getTen());
        Glide.with(Off_Video.this).load(nguoiNhan.getAnhDaiDien())
                .placeholder(R.drawable.avatar)
                .into(binding.avatarcall);
        binding.checkFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nguoiNhan != null)
                {
                    cancelInvitation(nguoiNhan.getToken());
                }
            }
        });



        CountDownTimer dem = new CountDownTimer(20000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if(binding.vongTrong.getVisibility() == View.INVISIBLE && binding.vongNgoai.getVisibility() == View.INVISIBLE )
                {
                    binding.vongTrong.setVisibility(View.VISIBLE);
                    binding.tinhtrang.setText("Đang Gọi.");
                }
                else if(binding.vongTrong.getVisibility() == View.VISIBLE && binding.vongNgoai.getVisibility() == View.INVISIBLE)
                {
                    binding.vongNgoai.setVisibility(View.VISIBLE);
                    binding.tinhtrang.setText("Đang Gọi..");
                }
                else
                {
                    binding.vongTrong.setVisibility(View.INVISIBLE);
                    binding.vongNgoai.setVisibility(View.INVISIBLE);
                    binding.tinhtrang.setText("Đang Gọi...");

                }

            }

            @Override
            public void onFinish() {
            }
        };

        dem.start();



        inviterToken = nguoiGui.getToken();
        if(meetingType != null && nguoiNhan != null)
        {

            initiateMeeting(meetingType, nguoiNhan.getToken());
        }

    }

    private void initiateMeeting(String meetingType, String receiverToken)
    {
        try{
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.KEY_NAME,nguoiGui.getTen());
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);
            data.put(Constants.KEY_ANH,nguoiGui.getAnhDaiDien());
            meetingRoom = nguoiGui.getMatKhau()+ "_" +
                    UUID.randomUUID().toString().substring(0,5);

            data.put(Constants.REMOTE_MSG_CALL_ROOM,meetingRoom);

            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

            sentRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION);


        }catch (Exception ex){
            Toast.makeText(this,"Loi meet",Toast.LENGTH_SHORT).show();
        }
    }

    private void sentRemoteMessage(String remoteMesssageBody, String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(),remoteMesssageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {

                if(response.isSuccessful()) {

                    if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                        Toast.makeText(Off_Video.this, "Hoàn Thành", Toast.LENGTH_SHORT).show();
                    } else if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
                        Toast.makeText(Off_Video.this,"Huy",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else {
                        Toast.makeText(Off_Video.this,"Loi truyen",Toast.LENGTH_SHORT).show();
                        finish();
                }

            }

            @Override
            public void onFailure(@NonNull  Call<String> call,@NonNull Throwable t) {

                Toast.makeText(Off_Video.this,"Loi",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    private void cancelInvitation(String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);


            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

            sentRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION_CANCELLED);
        } catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null)
            {
                if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED))
                {
                    try {

                        URL serverURL = new URL("https://meet.jit.si");
                        JitsiMeetConferenceOptions defaultOptions =
                                new JitsiMeetConferenceOptions.Builder()
                                        .setServerURL(serverURL)
                                        .setWelcomePageEnabled(false)
                                        .setRoom(meetingRoom)
                                        .build();

                        JitsiMeetActivity.launch(Off_Video.this,defaultOptions);
                        finish();

                    }catch (Exception ex){
                        Toast.makeText(Off_Video.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {
                    Toast.makeText(context, "tu choi", Toast.LENGTH_SHORT).show();
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