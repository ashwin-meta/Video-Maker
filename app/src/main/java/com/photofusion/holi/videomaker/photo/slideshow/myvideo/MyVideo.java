package com.photofusion.holi.videomaker.photo.slideshow.myvideo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ironsource.mediationsdk.IronSource;
import com.photofusion.holi.videomaker.photo.slideshow.R;
import com.photofusion.holi.videomaker.photo.slideshow.util.AdAdmob;
import com.photofusion.holi.videomaker.photo.slideshow.util.BaseActivity;
import com.photofusion.holi.videomaker.photo.slideshow.util.Prefs;
import com.photofusion.holi.videomaker.photo.slideshow.videoplayer.VideoPlayerActivity;


import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MyVideo extends BaseActivity {

    public static ArrayList<String> videoPath = new ArrayList<String>();

    RecyclerView videoListView;
    MyVideoAdapter videoAdapter;
    int FLAG_VIDEO = 21;
    ImageView backIV;
    RelativeLayout header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_videos);

//        setTV();

        header = (RelativeLayout) findViewById(R.id.header);
        videoLoader();

        backIV = (ImageView) findViewById(R.id.back);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Prefs prefs = new Prefs(this);
        if (prefs.getPremium() == 0) {
            AdAdmob adAdmob = new AdAdmob(this);
            adAdmob.BannerAd(findViewById(R.id.banner_container), this);
        }

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
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


    public void videoLoader() {
        getFromStorage();
        videoListView = (RecyclerView) findViewById(R.id.recyclerView);
        videoAdapter = new MyVideoAdapter(videoPath, MyVideo.this, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(MyVideo.this, VideoPlayerActivity.class);
                intent.putExtra("video_path", videoPath.get(position));
                startActivityForResult(intent, FLAG_VIDEO);
            }
        });

        videoListView.setLayoutManager(new GridLayoutManager(this, 2));
        videoListView.setItemAnimator(new DefaultItemAnimator());
        videoListView.setAdapter(videoAdapter);

    }

    public void getFromStorage() {
        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + getResources().getString(R.string.app_name);
        File file = new File(folder);
        videoPath = new ArrayList<String>();
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            Arrays.sort(listFile, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].getAbsolutePath().contains(".mp4")) {
                    videoPath.add(listFile[i].getAbsolutePath());
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLAG_VIDEO) {
            videoAdapter.notifyDataSetChanged();
            videoLoader();
        }
    }


}
