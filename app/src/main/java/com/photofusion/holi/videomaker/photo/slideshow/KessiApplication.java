package com.photofusion.holi.videomaker.photo.slideshow;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.photofusion.holi.videomaker.photo.slideshow.util.AdAdmob;
import com.photofusion.holi.videomaker.photo.slideshow.util.AppOpenManager;
import com.photofusion.holi.videomaker.photo.slideshow.util.Prefs;
import com.photofusion.holi.videomaker.photo.slideshow.util.SharedPrefs;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import vcarry.data.ImageData;
import vcarry.data.MusicData;
import vcarry.mask.KessiTheme;
import vcarry.util.EPreferences;
import vcarry.util.FileUtils;
import vcarry.util.PermissionModelUtil;

public class KessiApplication extends Application{


    public static Context global_ctx;
    public static int VIDEO_HEIGHT;
    public static int VIDEO_WIDTH;
    private static KessiApplication instance;
    public static boolean isBreak = false;
    public HashMap<String, ArrayList<ImageData>> allAlbum;
    private ArrayList<String> allFolder;
    int frame = 0;
    public boolean isEditEnable = false;
    public boolean isFromSdCardAudio = false;
    public int min_pos = Integer.MAX_VALUE;
    private MusicData PVMWSMusicData;
    private float second = 3.0f;
    private String selectedFolderId = "";
    public final ArrayList<ImageData> selectedImages = new ArrayList();
    public final ArrayList<ImageData> selectedImagesstart = new ArrayList();
    public KessiTheme selectedTheme = KessiTheme.LoveMix;
    public ArrayList<String> videoImages = new ArrayList();
    public static String[] startframelist, endframelist;

    public static KessiApplication getInstance() {
        return instance;
    }

    private void init() {
        if (!new PermissionModelUtil(this).needPermissionCheck()) {
            getFolderList();
            if (!FileUtils.APP_DIRECTORY.exists()) {
                FileUtils.APP_DIRECTORY.mkdirs();
            }

        }
        try {
            setVideoHeightWidth();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setVideoHeightWidth() {
        String s = getResources().getStringArray(R.array.video_height_width)
                [EPreferences.getInstance(getApplicationContext()).getInt(EPreferences.PREF_KEY_VIDEO_QUALITY, 2)];
        StringBuilder sb = new StringBuilder();
        sb.append("KessiApplication VideoQuality value  is:- ");
        sb.append(s);
        Log.d("TAG", sb.toString());
    }


    public Bitmap loadBitmapFromAssets(Context context, String path) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(path);
            return BitmapFactory.decodeStream(stream);
        } catch (Exception ignored) {
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public HashMap<String, ArrayList<ImageData>> getAllAlbum() {
        return this.allAlbum;
    }


    public String getCurrentTheme() {
        return getSharedPreferences("theme", 0).getString("current_theme", KessiTheme.ZoomOut.toString());
    }

    @SuppressLint("Range")
    public void getFolderList() {
        this.allFolder = new ArrayList();
        this.allAlbum = new HashMap();
        Cursor query = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id", "bucket_display_name", "bucket_id", "datetaken", "_data"}, (String) null, (String[]) null, "_data DESC");
        if (query.moveToFirst()) {
            int columnIndex = query.getColumnIndex("bucket_display_name");
            int columnIndex2 = query.getColumnIndex("bucket_id");
            setSelectedFolderId(query.getString(columnIndex2));
            do {
                ImageData PVMWSImageData = new ImageData();
                PVMWSImageData.imagePath = query.getString(query.getColumnIndex("_data"));
                PVMWSImageData.imageThumbnail = query.getString(query.getColumnIndex("_data"));
                if (!PVMWSImageData.imagePath.endsWith(".gif")) {
                    String string = query.getString(columnIndex);
                    String string2 = query.getString(columnIndex2);
                    if (!this.allFolder.contains(string2)) {
                        this.allFolder.add(string2);
                    }
                    ArrayList<ImageData> list = (ArrayList) this.allAlbum.get(string2);
                    if (list == null) {
                        list = new ArrayList();
                    }
                    PVMWSImageData.folderName = string;
                    list.add(PVMWSImageData);
                    this.allAlbum.put(string2, list);
                }
            } while (query.moveToNext());
        }
    }

    public int getFrame() {
        return this.frame;
    }

    public MusicData getMusicData() {
        return this.PVMWSMusicData;
    }


    public float getSecond() {
        return this.second;
    }



    public ArrayList<ImageData> getSelectedImages() {
        return this.selectedImages;
    }

    public ArrayList<ImageData> getSelectedImagesstart() {
        return this.selectedImagesstart;
    }


    public void initArray() {
        this.videoImages = new ArrayList();
    }

    private static final String ONESIGNAL_APP_ID = "0a787893-4df4-4f3b-ad5c-2ead8af46b59";

    AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();

            global_ctx = this;

        AudienceNetworkAds.initialize(this);

        AdSettings.setTestMode(true);

        if (SharedPrefs.getREMOTE_AD(this).equals("a"))
        {
            AdAdmob.REMOTE_AD = "a";
        }
        else if(SharedPrefs.getREMOTE_AD(this).equals("f"))
        {
            AdAdmob.REMOTE_AD = "f";
        }
        Log.e( "onCreateop: ",AdAdmob.REMOTE_AD );


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            builder.detectFileUriExposure();
            StrictMode.setVmPolicy(builder.build());
        }

        instance = this;

        try {
            startframelist = getAssets().list("startframe");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            endframelist = getAssets().list("endframe");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        init();

//        // Enable verbose OneSignal logging to debug issues if needed.
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
//
//        // OneSignal Initialization
//        OneSignal.initWithContext(this);
//        OneSignal.setAppId(ONESIGNAL_APP_ID);

        Prefs prefs = new Prefs(this);
        if (prefs.getPremium() == 0 && AdAdmob.REMOTE_AD.equals("a")) {

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {

                }
            });


//            MobileAds.initialize(this);
//            AdmobNativeAdController.getInstance().initialize(this);

            appOpenManager = new AppOpenManager(this);

        }


    }



    public void setCurrentTheme(String s) {
        Editor edit = getSharedPreferences("theme", 0).edit();
        edit.putString("current_theme", s);
        edit.commit();
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public void setMusicData(MusicData PVMWSMusicData) {
        this.PVMWSMusicData = PVMWSMusicData;
    }



    public void setSecond(float second) {
        this.second = second;
    }

    public void setSelectedFolderId(String selectedFolderId) {
        this.selectedFolderId = selectedFolderId;
    }


}
