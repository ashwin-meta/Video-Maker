package com.photofusion.holi.videomaker.photo.slideshow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ironsource.mediationsdk.IronSource;
import com.photofusion.holi.videomaker.photo.slideshow.util.AdAdmob;
import com.photofusion.holi.videomaker.photo.slideshow.util.BaseActivity;
import com.photofusion.holi.videomaker.photo.slideshow.util.KSUtil;
import com.photofusion.holi.videomaker.photo.slideshow.util.Prefs;
import com.photofusion.holi.videomaker.photo.slideshow.videoplayer.VideoPlayerActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import vcarry.util.FileUtils;
import vcarry.util.Utils;

public class VideoMakerActivity extends BaseActivity {
    TextView perTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_maker);
//        setTV();

        perTV = findViewById(R.id.perTV);
        new ProcessVideo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Prefs prefs = new Prefs(this);
        if (prefs.getPremium() == 0) {
            AdAdmob adAdmob = new AdAdmob(this);
            adAdmob.BannerAd(findViewById(R.id.banner_container), this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Prefs prefs = new Prefs(this);
        if (prefs.getPremium() != 0) {

            findViewById(R.id.banner_container).setVisibility(View.GONE);
        }
        IronSource.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // call the IronSource onPause method
        IronSource.onPause(this);
    }


//    public void setTV(){
//        LinearLayout adContainer = findViewById(R.id.banner_container);
//        if (!AdAdmob.isloadFbMAXAd) {
//            //admob
//            AdAdmob.initAd(VideoMakerActivity.this);
//            AdAdmob.adptiveBannerAd(VideoMakerActivity.this, adContainer);
//        } else {
//            //MAX + Fb banner Ads
//            AdAdmob.initMAX(VideoMakerActivity.this);
//            AdAdmob.maxBannerAdaptive(VideoMakerActivity.this, adContainer);
//        }
//    }

    public class ProcessVideo extends AsyncTask<Integer, Integer, List<String>> {
        File imgDir;
        VideoEncoderHelper videoEncoder;

        @Override
        protected void onPreExecute() {

        }


        protected List<String> doInBackground(Integer... params) {

            new File(FileUtils.TEMP_DIRECTORY, "video.txt").delete();
            if (!VideoThemeActivity.logFile.exists()) {
                try {
                    VideoThemeActivity.logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            imgDir = FileUtils.getImageDirectory(VideoThemeActivity.application.selectedTheme
                    .toString());
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
            String formattedDate = df.format(c.getTime());

            VideoThemeActivity.outputPath = VideoThemeActivity.folderPath;
            File file = new File(VideoThemeActivity.outputPath);
            if (!file.exists())
                file.mkdirs();
            VideoThemeActivity.outputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/" + getResources().getString(R.string.app_name)
                    + "/" + "video_" + formattedDate + ".mp4";
            int finalwidth = KessiApplication.VIDEO_WIDTH;

            VideoThemeActivity.duration1 = Float.parseFloat(""
                    + String.valueOf(VideoThemeActivity.total)
                    .replace(" Seconds", "")) * 1000;

            // Prepare video encoding parameters
            String audioPath = VideoThemeActivity.application.getMusicData() != null ? 
                              VideoThemeActivity.application.getMusicData().track_data : null;
            Bitmap frameOverlay = null;
            
            // Load frame overlay if selected
            if (Utils.framePostion > -1 && FileUtils.frameFile.exists()) {
                try {
                    frameOverlay = BitmapFactory.decodeFile(FileUtils.frameFile.getAbsolutePath());
                    Log.v("withframe", "Frame overlay loaded");
                } catch (Exception e) {
                    Log.e("VideoMaker", "Error loading frame", e);
                }
            } else {
                Log.v("withoutframe", "No frame overlay");
            }

            // Calculate duration per image in milliseconds
            float durationPerImageMs = VideoThemeActivity.application.getSecond() * 1000.0f;
            float totalDurationSec = VideoThemeActivity.total;
            int imageCount = KSUtil.videoPathList.size();

            // Create video encoder
            videoEncoder = new VideoEncoderHelper();
            
            // Start encoding
            videoEncoder.createVideoFromImages(
                imgDir,
                VideoThemeActivity.outputPath,
                audioPath,
                frameOverlay,
                KessiApplication.VIDEO_WIDTH,
                KessiApplication.VIDEO_HEIGHT,
                durationPerImageMs,
                totalDurationSec,
                imageCount,
                new VideoEncoderHelper.ProgressCallback() {
                    @Override
                    public void onProgress(int progress) {
                        runOnUiThread(() -> {
                            perTV.setText(progress + " %");
                        });
                    }

                    @Override
                    public void onComplete(boolean success) {
                        runOnUiThread(() -> {
                            if (success) {
                                perTV.setText("100 %");
                                // Clean up temporary files
                                removeFrameImage(VideoThemeActivity.folderPath);
                                removeFrameImage(VideoThemeActivity.folderPath + "/temp");
                                removeFrameImage(VideoThemeActivity.folderPath + "/edittmpzoom");
                                FileUtils.deleteFile(VideoThemeActivity.tempFile);
                                removemusic(VideoThemeActivity.folderPath + "/music/");
                                
                                // Scan media file
                                File f = new File(VideoThemeActivity.outputPath);
                                MediaScannerConnection.scanFile(getApplicationContext(),
                                        new String[]{f.getAbsolutePath()},
                                        new String[]{"mp4"}, null);

                                // Navigate to video player
                                VideoThemeActivity.handler.postDelayed(runnable, 1000);
                            } else {
                                Toast.makeText(VideoMakerActivity.this, 
                                             "Error creating video", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            );

            return null;

        }


        @Override
        protected void onPostExecute(List<String> result) {


        }

        @Override
        protected void onProgressUpdate(final Integer... values) {

        }
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            VideoThemeActivity.handler.removeCallbacks(runnable);

            Intent in = new Intent(VideoMakerActivity.this,
                    VideoPlayerActivity.class);
            in.putExtra("video_path", VideoThemeActivity.outputPath);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
            AdAdmob adAdmob = new AdAdmob( VideoMakerActivity.this);
            adAdmob.FullscreenAd(VideoMakerActivity.this,SplashActivity.adIdsis,"");
        }
    };


    void removeFrameImage(String path) {
        File appimages = new File(path);
        if (appimages.exists()) {
            File[] files = appimages.listFiles();
            if (files != null) {
                for (File f : files) {
                    if ((f.getName().endsWith(".jpg") || f.getName().endsWith(
                            ".png"))) {
                        f.delete();
                    }
                }
            }
        }
    }

    void removemusic(String path) {
        File appimages = new File(path);
        if (appimages.exists()) {
            File[] files = appimages.listFiles();
            if (files != null) {
                for (File f : files) {
                    if ((f.getName().endsWith(".mp3"))) {
                        f.delete();
                    }
                }
            }
        }
    }

    boolean back = false;
    @Override
    public void onBackPressed() {
        if (back) {
            super.onBackPressed();
        }
    }


}
