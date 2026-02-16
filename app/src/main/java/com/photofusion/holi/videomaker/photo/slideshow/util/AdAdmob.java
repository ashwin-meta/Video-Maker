package com.photofusion.holi.videomaker.photo.slideshow.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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


public class AdAdmob {


    public static String REMOTE_AD = "a";

    public static AlertDialog ProgressDialog;

    public AdAdmob(Activity activity) {

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


//    private AdView mAdView;
//
//    public void loadAd(Activity activity) {
//        mAdView = new AdView(activity);
//        mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdUnitId(activity.getString(R.string.admob_banner_id));
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        mAdView.loadAd(adRequest);
//
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Ad is loaded successfully
//                Log.e( "showAd: ","succ" );
//                Log.e( "showAd: ","hiiiii"+mAdView );
//            }
//
//            @Override
//            public void onAdFailedToLoad(LoadAdError loadAdError) {
//                // Ad failed to load, handle the error
//                mAdView.destroy();
//            }
//        });
//    }
//
//    public void showAd(final LinearLayout Ad_Layout) {
//        Log.e( "showAd: ","hii"+mAdView );
//        if (mAdView != null ) {
//            Log.e( "showAd: ","loadde" );
//            Ad_Layout.addView(mAdView);
//            Ad_Layout.setVisibility(View.VISIBLE);
//        }
//    }

// Call loadAd() when you want to load the ad
// Call showAd() when you want to show the ad


    public void BannerAd(final LinearLayout Ad_Layout, Activity activity) {
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
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Ad_Layout.setVisibility(View.GONE);


                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    Ad_Layout.setVisibility(View.GONE);


                }
            });
        } else if (AdAdmob.REMOTE_AD.equals("f")) {
            BanneraFb(activity);
        }


    }

    public static void BanneraFb(Activity activity) {
        adViewb = new com.facebook.ads.AdView(activity, activity.getString(R.string.fb_bannerad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);


        LinearLayout adContainer = activity.findViewById(R.id.banner_container);


        adContainer.addView(adViewb);


        adViewb.loadAd();

    }


    static InterstitialAd interstitialAda;

    public static void FullscreenAd(final Activity activity, List<String> adIds, String intent) {
        // TODO Auto-generated method stub
        Prefs prefs = new Prefs(activity);
        if (prefs.getPremium() != 0) {
            return;
        }

        Ad_Popup(activity);

        if (REMOTE_AD.equals("a")) {

            if (interstitialAda != null) {
                Log.e( "FullscreenAd: ","111111111" );
                interstitialAda.show(activity);
                ProgressDialog.dismiss();
                interstitialAda.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.e( "onAdDismissedFullScreenContent: ","loaded" );
                        if (intent.equals("mycreation")) {
                            Intent intent = new Intent(activity, MyVideo.class);
                            activity.startActivity(intent);

                        }
                    }
                });
                return;
            }

            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(activity, adIds.get(0), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    Log.e( "onAdDismissedFullScreenContent: ","loaded11" );
                                    if (intent.equals("mycreation")) {
                                        Intent intent = new Intent(activity, MyVideo.class);
                                        activity.startActivity(intent);

                                    }
                                }
                            });
                            interstitialAda = interstitialAd;
                            interstitialAd.show(activity);
                            ProgressDialog.dismiss();
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("onAdFailedToLoad: ", "hiii");
                            // Handle the error
                            ProgressDialog.dismiss();
                            if (adIds.size() > 1) {
                                // If ad loading fails for the current ad ID, try the next ad ID in the list
                                adIds.remove(0);
                                FullscreenAd(activity, adIds, intent);
                            }

                        }
                    });


        } else if (AdAdmob.REMOTE_AD.equals("f")) {
            fbInter(activity, intent);
        } else {
            ProgressDialog.dismiss();
        }
    }


    public static com.facebook.ads.InterstitialAd interstitialAd;

    public static void fbInter(Activity activity, String intent) {

        interstitialAd = new com.facebook.ads.InterstitialAd(activity, activity.getString(R.string.fb_interad));
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

                if (intent.equals("mycreation")) {
                    AdAdmob.ProgressDialog.dismiss();
                    Intent intent = new Intent(activity, MyVideo.class);
                    activity.startActivity(intent);

                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                AdAdmob.ProgressDialog.dismiss();

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed

                // Show the ad
                interstitialAd.show();
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

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }


    public static void Ad_Popup(Context context) {
        ProgressDialog = null;
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_loader, null);
        AlertDialog.Builder alert = null;
        alert = new AlertDialog.Builder(context);
        alert.setView(view);

        ProgressDialog = alert.create();

        ProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ProgressDialog.setCancelable(false);

        ProgressDialog.show();

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

        } else if (AdAdmob.REMOTE_AD.equals("f")) {
            loadNativeAdfb(context);
        }
    }

    public static NativeAdLayout nativeAdLayout;
    public static LinearLayout adView;
    public static com.facebook.ads.NativeAd nativeAdfb;
    public static com.facebook.ads.AdView adViewb;

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
                Log.e("onError: ", "1" + adError);
                Log.e("onError: ", "2" + ad);
            }

            @Override
            public void onAdLoaded(Ad ad) {

                // showing Toast message
//                Toast.makeText(activity, "onAdLoaded", Toast.LENGTH_SHORT).show();

                if (nativeAdfb == null || nativeAdfb != ad) {
                    return;
                }

                // Inflate Native Ad into Container
                inflateAd(nativeAdfb, activity);

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

        nativeAdLayout = activity.findViewById(R.id.native_ad_container);

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


}





