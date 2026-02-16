package com.photofusion.holi.videomaker.photo.slideshow.util;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.photofusion.holi.videomaker.photo.slideshow.KessiApplication;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KessiApplication.global_ctx = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        KessiApplication.global_ctx = this;
    }
}

