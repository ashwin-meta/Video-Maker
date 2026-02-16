package com.photofusion.holi.videomaker.photo.slideshow;

import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.photofusion.holi.videomaker.photo.slideshow.util.AdAdmob;
import com.photofusion.holi.videomaker.photo.slideshow.util.BaseActivity;
import com.photofusion.holi.videomaker.photo.slideshow.util.Prefs;

public class StartActivity extends BaseActivity {

    CardView startBtn, privacyBtn;


    public static String ImageUri = "";
    public static String DownloadUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Prefs prefs = new Prefs(this);
        if (prefs.getPremium() == 0) {
            dialogHomeAd();
        }




        startBtn = findViewById(R.id.btn_taptostart);
//        rateBtn = findViewById(R.id.rate_Btn);
//        shareBtn = findViewById(R.id.share_Btn);
        privacyBtn = findViewById(R.id.btn_privacyy);


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();
            }
        });

//        rateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rateApp();
//            }
//        });


//        shareBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareApp();
//            }
//        });


        privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, PrivacyActivity.class));
            }
        });


        if (prefs.getPremium() == 0) {
            AdAdmob adAdmob = new AdAdmob(this);
            adAdmob.BannerAd(findViewById(R.id.banner_container), this);
        }

    }


    public void dialogHomeAd() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.homer_ad);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }

        ImageView ad_img = dialog.findViewById(R.id.ad_img);
        Glide.with(this).load(Uri.parse(ImageUri)).into(ad_img);

        ((Button) dialog.findViewById(R.id.okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DownloadUri)));
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void rateApp() {
        Uri uri = Uri.parse("market://details?id="
                + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.rateApp)
                            + getApplicationContext().getPackageName())));
        }
    }


    public void shareApp() {
        String shareBody = getString(R.string.sahereApp)
                + getApplicationContext().getPackageName();
        Intent sharingIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent
                .putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.ShareappTost));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
