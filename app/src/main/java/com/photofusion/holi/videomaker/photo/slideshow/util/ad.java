package com.photofusion.holi.videomaker.photo.slideshow.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.photofusion.holi.videomaker.photo.slideshow.R;
import com.photofusion.holi.videomaker.photo.slideshow.myvideo.MyVideo;

import java.util.ArrayList;
import java.util.List;

public class ad {

    public static String REMOTE_AD = "a";
    public static AlertDialog ProgressDialog;
    public ad(Activity activity) {

        Prefs prefs = new Prefs(activity);
        if (prefs.getPremium() != 0) {
            return;
        }
        if (REMOTE_AD.equals("a")) {

            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        } else if (AdAdmob.REMOTE_AD.equals("f")) {
            initFb(activity);
        }


    }
    public void initFb(Activity activity) {
        AudienceNetworkAds.initialize(activity);
        AdSettings.setTestMode(true);
    }
    public void ShowBannerAd(final LinearLayout Ad_Layout, Activity activity) {
        Prefs prefs = new Prefs(activity);
        if (prefs.getPremium() != 0) {
            return;
        }

        if (REMOTE_AD.equals("a")) {
            final AdView mAdView = new AdView(activity);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(activity.getString(R.string.admob_banner_id));
            AdRequest adore = new AdRequest.Builder().build();
            mAdView.loadAd(adore);
            Ad_Layout.addView(mAdView);


            mAdView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    Ad_Layout.setVisibility(View.VISIBLE);
                    super.onAdLoaded();

                    Log.e("ddddd", "dddd");
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Ad_Layout.setVisibility(View.GONE);
                    Log.e("ddddd1", "dddd");

                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    Ad_Layout.setVisibility(View.GONE);
                    Log.e("ddddd2", "dddd" + loadAdError.getMessage());

                }
            });
        } else if (AdAdmob.REMOTE_AD.equals("f")) {
            BanneraFb(activity);
        }


    }
    public static com.facebook.ads.AdView adViewFb;
    public static void BanneraFb(Activity activity) {
        adViewFb = new com.facebook.ads.AdView(activity, activity.getString(R.string.fb_bannerad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);


        LinearLayout adContainer = activity.findViewById(R.id.banner_container);


        adContainer.addView(adViewFb);


        adViewFb.loadAd();

    }
    static InterstitialAd mInterstitialAd;
    public static com.facebook.ads.InterstitialAd interstitialAd;
    public static void loadInterAd(Context context) {

        if (REMOTE_AD.equals("a")) {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, context.getString(R.string.admob_interstitial), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    mInterstitialAd = null;
                    ProgressDialog.dismiss();
                }
            });

        }else if (AdAdmob.REMOTE_AD.equals("f")) {
            interstitialAd = new com.facebook.ads.InterstitialAd(context, context.getString(R.string.fb_interad));

            // Create listeners for the Interstitial Ad
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    // Interstitial ad displayed callback
                    AdAdmob.ProgressDialog.dismiss();
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback

                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Ad error callback
                    AdAdmob.ProgressDialog.dismiss();
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    AdAdmob.ProgressDialog.dismiss();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                }
            };

            interstitialAd.loadAd(
                    interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        }



    }
    public void showInterstitialAd(Activity activity,String intent) {

        Prefs prefs = new Prefs(activity);
        if (prefs.getPremium() != 0) {
            return;
        }

        if (REMOTE_AD.equals("a")) {
            if (mInterstitialAd != null ) {

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadInterAd(activity);
                        if (intent.equals("mywork")) {
                            AdAdmob.ProgressDialog.dismiss();
                            Intent intent1 = new Intent(activity, MyVideo.class);
                            activity.startActivity(intent1);
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                        ProgressDialog.dismiss();
                    }


                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitialAd = null;

                    }
                });
                if (mInterstitialAd != null) {
                    ProgressDialog.dismiss();
                    mInterstitialAd.show((Activity) activity);
                }


            } else {
                if (intent.equals("mywork")) {
                    AdAdmob.ProgressDialog.dismiss();
                    Intent intent1 = new Intent(activity, MyVideo.class);
                    activity.startActivity(intent1);
                }
            }
        }else if (AdAdmob.REMOTE_AD.equals("f")) {
            if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                interstitialAd.show();
                if (intent.equals("mywork")) {
                    AdAdmob.ProgressDialog.dismiss();
                    Intent intent1 = new Intent(activity, MyVideo.class);
                    activity.startActivity(intent1);
                    loadInterAd(activity);
                }
            }
        }



    }




    public void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());


        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);

    }


    public void loadNativeAd(final Activity context, final RelativeLayout frameLayout) {
        Prefs prefs = new Prefs(context);
        if (prefs.getPremium() != 0) {
            return;
        }

        if (REMOTE_AD.equals("a")) {
            AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.admob_native_ad_id))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {

                            NativeAdView adView = (NativeAdView) context.getLayoutInflater()
                                    .inflate(R.layout.ad_lay, null);
                            // This method sets the text, images and the native ad, etc into the ad
                            // view.
                            populateNativeAdView(nativeAd, adView);
                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);
                        }
                    });

            VideoOptions videoOptions =
                    new VideoOptions.Builder().setStartMuted(true).build();

            NativeAdOptions adOptions =
                    new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(
                            new AdListener() {
                                @Override
                                public void onAdFailedToLoad(LoadAdError loadAdError) {
                                }
                            })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());

        }
        else if (AdAdmob.REMOTE_AD.equals("f")) {
            loadNativeAdfb(context);
        }
    }



//    public void loadNativeAd(Activity context,final RelativeLayout frameLayout) {
//
//        if (REMOTE_AD.equals("a")) {
//
//
//        }  else if (AdAdmob.REMOTE_AD.equals("f")) {
//
//        }
//    }
//
//
//
//
//    public void showNativeAd(Activity context) {
//        Prefs prefs = new Prefs(context);
//        if (prefs.getPremium() != 0) {
//            return;
//        }
//
//        if (REMOTE_AD.equals("a")) {
//
//
//        } else if (AdAdmob.REMOTE_AD.equals("f")) {
//
//
//        }
//
//    }


    public static NativeAdLayout fbNativeAdLayout;
    public static LinearLayout fbAdView;
    public static com.facebook.ads.NativeAd nativeAdfb;
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

        fbNativeAdLayout = activity.findViewById(R.id.native_ad_container);

        LayoutInflater inflater = LayoutInflater.from(activity);

        // Inflate the Ad view.
        fbAdView = (LinearLayout) inflater.inflate(R.layout.fbnative, fbNativeAdLayout, false);

        // adding view
        fbNativeAdLayout.addView(fbAdView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = activity.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, fbNativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        com.facebook.ads.MediaView nativeAdIcon = fbAdView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = fbAdView.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView nativeAdMedia = fbAdView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = fbAdView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = fbAdView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = fbAdView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = fbAdView.findViewById(R.id.native_ad_call_to_action);

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
        nativeAd.registerViewForInteraction(fbAdView, nativeAdMedia, nativeAdIcon, clickableViews);
    }





}
