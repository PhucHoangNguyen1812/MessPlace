package com.doanjava.messbcode.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.doanjava.messbcode.Adapters.ChuyenDoiSlider;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ActivityWelcomeBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class Welcome extends AppCompatActivity {


    ActivityWelcomeBinding binding;
    int[] images = {R.drawable.item1,
    R.drawable.item2,
    R.drawable.item3,
    R.drawable.item4
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        ChuyenDoiSlider chuyenDoiSlider = new ChuyenDoiSlider(images);
        binding.imageSlider.setSliderAdapter(chuyenDoiSlider);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        binding.imageSlider.startAutoCycle();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this,KichHoatSoDienThoai.class);
                startActivity(intent);
            }
        });
    }
}