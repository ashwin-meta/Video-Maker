package com.photofusion.holi.videomaker.photo.slideshow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.photofusion.holi.videomaker.photo.slideshow.util.BaseActivity;

public class DAActivity extends BaseActivity {

    ImageView mainBg, insIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daactivity);

        mainBg = findViewById(R.id.mainBg);
        Glide.with(this)
                .load(R.drawable.addbg)
                .into(mainBg);

        insIV = findViewById(R.id.insIV);
        insIV.setOnClickListener(v -> {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.kessi.msvideomaker")));
        });

        Animation shake = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        insIV.setAnimation(shake);
        shake.setRepeatCount(-1);

    }
}