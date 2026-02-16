package com.photofusion.holi.videomaker.photo.slideshow;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.photofusion.holi.videomaker.photo.slideshow.kessiphotopicker.activity.ImagePickerActivity;
import com.photofusion.holi.videomaker.photo.slideshow.myvideo.MyVideo;
import com.photofusion.holi.videomaker.photo.slideshow.swap.SwapperActivity;
import com.photofusion.holi.videomaker.photo.slideshow.util.AdAdmob;
import com.photofusion.holi.videomaker.photo.slideshow.util.Animatee;
import com.photofusion.holi.videomaker.photo.slideshow.util.BaseActivity;
import com.photofusion.holi.videomaker.photo.slideshow.util.KSUtil;
import com.photofusion.holi.videomaker.photo.slideshow.util.Prefs;
import com.photofusion.holi.videomaker.photo.slideshow.util.PurchaseActivity;
import com.photofusion.holi.videomaker.photo.slideshow.util.notification.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    String[] permissionsList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    String[] permissionsList13 = new String[]{Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.POST_NOTIFICATIONS};

    CardView   shareIV, privacyIV, moreIV;

    RelativeLayout rateIV;

    LinearLayout btnStart,btnAllVideo;


    CardView Premium;

    public static NativeAdLayout nativeAdLayout;
    public static LinearLayout adView;
    public static com.facebook.ads.NativeAd nativeAdfb;

    public static Activity activity;


    private void setDailyAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm to trigger at a fixed time every day
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,10); // Set the hour (in 24-hour format)
        calendar.set(Calendar.MINUTE, 0); // Set the minute
        calendar.set(Calendar.SECOND, 0); // Set the second

        // Check if the selected time has already passed today
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            // If it has, add one day to the alarm time
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use setAndAllowWhileIdle() for Android 6.0 (Marshmallow) and above
            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            // Use set() for pre-Marshmallow devices
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Prefs prefs = new Prefs(this);

        activity = MainActivity.this;

        setDailyAlarm();

        init();







        if (prefs.getPremium() == 0) {



            AdAdmob adAdmob = new AdAdmob((Activity) this);
            adAdmob.loadNativeAd((Activity) this, findViewById(R.id.container));
            adAdmob.loadNativeAd((Activity) this, findViewById(R.id.container1));
        } else if (prefs.getPremium() == 1) {
            findViewById(R.id.adProgress).setVisibility(View.GONE);
            findViewById(R.id.native_ad_container).setVisibility(View.GONE);
            findViewById(R.id.native_ad_container1).setVisibility(View.GONE);

        }

        Premium = findViewById(R.id.remove_ads);
        Premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PurchaseActivity.class));

            }
        });


        if (prefs.getPremium() == 0) {
            if (AdAdmob.REMOTE_AD.equals("f")) {
                loadNativeAdfb(this);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Prefs prefs = new Prefs(this);
        if (prefs.getPremium() != 0) {
            findViewById(R.id.container).setVisibility(View.GONE);
            findViewById(R.id.container1).setVisibility(View.GONE);
            findViewById(R.id.native_ad_container).setVisibility(View.GONE);
            findViewById(R.id.native_ad_container1).setVisibility(View.GONE);
        }

    }

    private void loadNativeAdfb(Activity activity) {

        // initializing nativeAd object
        nativeAdfb = new com.facebook.ads.NativeAd(activity, activity.getString(R.string.fb_native_ad_id));

        // creating  NativeAdListener
        NativeAdListener nativeAdListener = new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {
                // showing Toast message
//                Toast.makeText(activity, "onMediaDownloaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // showing Toast message
//                Toast.makeText(activity, ""+adError, Toast.LENGTH_SHORT).show();
                Log.e( "onError: ","1"+adError );
                Log.e( "onError: ","2"+ad );
            }

            @Override
            public void onAdLoaded(Ad ad) {

                // showing Toast message
//                Toast.makeText(activity, "onAdLoaded", Toast.LENGTH_SHORT).show();

                if (nativeAdfb == null || nativeAdfb != ad) {
                    return;
                }

                // Inflate Native Ad into Container
                inflateAd(nativeAdfb,activity);

//                if (HomeActivity.isad) {
//                    inflateAd1(nativeAdfb, activity);
//                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                // showing Toast message
//                Toast.makeText(activity, "onAdClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // showing Toast message
//                Toast.makeText(activity, "onLoggingImpression", Toast.LENGTH_SHORT).show();
            }
        };

        // Load an ad
        nativeAdfb.loadAd(
                nativeAdfb.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }
    void inflateAd(com.facebook.ads.NativeAd nativeAd, Activity activity) {

        // unregister the native Ad View
        nativeAdfb.unregisterView();

        // Add the Ad view into the ad container.

        nativeAdLayout = activity.findViewById(R.id.native_ad_container1);

        LayoutInflater inflater = LayoutInflater.from(activity);

        // Inflate the Ad view.
        adView = (LinearLayout) inflater.inflate(R.layout.fbnative, nativeAdLayout, false);

        // adding view
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = activity.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Setting  the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and  button to listen for clicks.
        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }




    void init() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        KessiApplication.VIDEO_HEIGHT = displaymetrics.widthPixels;
        KessiApplication.VIDEO_WIDTH = displaymetrics.widthPixels;


        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        btnAllVideo = findViewById(R.id.btnAllVideo);
        btnAllVideo.setOnClickListener(this);

        rateIV = findViewById(R.id.btn_rateme);
        rateIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs();
            }
        });

        shareIV = findViewById(R.id.shareIV);
        shareIV.setOnClickListener(this);

        privacyIV = findViewById(R.id.privacyIV);
        privacyIV.setOnClickListener(this);

        moreIV = findViewById(R.id.moreIV);
        moreIV.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:

                KSUtil.Themeposs.clear();
                KSUtil.Frameposs.clear();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!checkPermissions(this, permissionsList13)) {
                        ActivityCompat.requestPermissions(this, permissionsList13, 333);
                    } else {
                        KSUtil.fromAlbum = false;
                        Intent mIntent = new Intent(MainActivity.this, ImagePickerActivity.class);
                        mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MAX_IMAGE, 30);
                        mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MIN_IMAGE, 4);
                        startActivityForResult(mIntent, ImagePickerActivity.PICKER_REQUEST_CODE);
                    }
                }

                if (!checkPermissions(this, permissionsList)) {
                    ActivityCompat.requestPermissions(this, permissionsList, 21);
                } else {
                    KSUtil.fromAlbum = false;
                    Intent mIntent = new Intent(MainActivity.this, ImagePickerActivity.class);
                    mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MAX_IMAGE, 30);
                    mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MIN_IMAGE, 4);
                    startActivityForResult(mIntent, ImagePickerActivity.PICKER_REQUEST_CODE);
                }
                break;

            case R.id.btnAllVideo:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!checkPermissions(this, permissionsList13)) {
                        ActivityCompat.requestPermissions(this, permissionsList13, 333);
                    } else {
                        KSUtil.fromAlbum = true;
                        Prefs prefs = new Prefs(this);
                        if (prefs.getPremium() != 0) {
                            startActivity(new Intent(this, MyVideo.class));
                        } else {
                            Animatee.animateSlideUp(MainActivity.this);
                            AdAdmob adAdmob = new AdAdmob((Activity) this);
                            adAdmob.FullscreenAd((Activity) this, SplashActivity.adIdsis, "mycreation");
                        }

                    }
                    return;
                }

                if (!checkPermissions(this, permissionsList)) {
                    ActivityCompat.requestPermissions(this, permissionsList, 22);
                } else {
                    KSUtil.fromAlbum = true;
                    startActivityForResult(new Intent(MainActivity.this, MyVideo.class), 0);
                    Animatee.animateSlideUp(MainActivity.this);
                    Prefs prefs = new Prefs(this);
                    if (prefs.getPremium() != 0) {
                        startActivity(new Intent(this, MyVideo.class));
                    } else {
                        Animatee.animateSlideUp(MainActivity.this);
//                        AdAdmob adAdmob = new AdAdmob((Activity) this);
                        AdAdmob.FullscreenAd((Activity) this, SplashActivity.adIdsis, "mycreation");
                    }
                }
                break;



            case R.id.shareIV:
                shareApp();
                break;

            case R.id.privacyIV:
                startActivityForResult(new Intent(MainActivity.this, PrivacyActivity.class), 0);
                break;

            case R.id.moreIV:
                moreApp();
                break;

            default:
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    public void moreApp() {
        startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/dev?id=7081479513420377164&hl=en")));
    }

    public void shareApp() {
        String shareBody = "https://play.google.com/store/apps/details?id="
                + getApplicationContext().getPackageName();

        Intent sharingIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent
                .putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "(This app is for making beautiful video from photos. Open it in Google Play Store to Download the Application)");

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void rateUs() {
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
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + getApplicationContext().getPackageName())));
        }
    }


    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 21) {

            if (checkPermissions(this, permissionsList)) {
                KSUtil.fromAlbum = false;
                Intent mIntent = new Intent(MainActivity.this, ImagePickerActivity.class);
                mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MAX_IMAGE, 30);
                mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MIN_IMAGE, 4);

            }
        }

        if (requestCode == 333) {

            if (checkPermissions(this, permissionsList13)) {
                KSUtil.fromAlbum = false;
                Intent mIntent = new Intent(MainActivity.this, ImagePickerActivity.class);
                mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MAX_IMAGE, 30);
                mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MIN_IMAGE, 4);

            }
        }

        if (requestCode == 22) {
            KSUtil.fromAlbum = true;
            startActivityForResult(new Intent(MainActivity.this, MyVideo.class), 0);
            Animatee.animateSlideUp(MainActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == ImagePickerActivity.PICKER_REQUEST_CODE) {
            KSUtil.videoPathList.clear();
            KSUtil.videoPathList = data.getExtras().getStringArrayList(ImagePickerActivity.KEY_DATA_RESULT);//ImagePickerActivity.saveFiles;
            if (KSUtil.videoPathList != null && !KSUtil.videoPathList.isEmpty()) {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < KSUtil.videoPathList.size(); i++) {
                    sb.append("Image Path" + (i + 1) + ":" + KSUtil.videoPathList.get(i));
                    sb.append("\n");

                }
                Log.e("Image", sb.toString());

                startActivityForResult(new Intent(MainActivity.this, SwapperActivity.class), 0);
//                AdAdmob adAdmob = new AdAdmob((Activity) this);
//                adAdmob.FullscreenAd((Activity) this, SplashActivity.adIdsis, "");
            }
        }
    }


}
