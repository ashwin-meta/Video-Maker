package com.photofusion.holi.videomaker.photo.slideshow.select_frm;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photofusion.holi.videomaker.photo.slideshow.KessiApplication;
import com.photofusion.holi.videomaker.photo.slideshow.LayManager;
import com.photofusion.holi.videomaker.photo.slideshow.VideoThemeActivity;
import com.photofusion.holi.videomaker.photo.slideshow.R;
import com.photofusion.holi.videomaker.photo.slideshow.util.BaseActivity;
import com.photofusion.holi.videomaker.photo.slideshow.util.KSUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import vcarry.data.ImageData;

public class DecorFrmActivity extends BaseActivity {


    Bitmap firstBit, endBit;
    MyCustomLayoutManager hrlManagaer2, hrlManagaer;
    StartFrameAdapter sFrmAdapter;
    EndFrameAdapter eFrmAdapter;
    RecyclerView startFrmRecycle, endFrmRecycle;
    Bitmap startFrameBitmap, endFrameBitmap;

    ImageView done, back, startIV, endIV;
    ImageView startframebtn, endframebtn;
    public static ArrayList<ImageData> arrayListmain;
    KessiApplication application;
    public static int posstart;

    ImageView img_btn_yes, img_btn_no;
    LinearLayout maindailog;


    RelativeLayout header, footer;
    LinearLayout tabLay;
    ArrayList<String> allPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decorefrm);

//        setTV();

        this.application = KessiApplication.getInstance();
        application.isEditEnable = true;
        allPath = new ArrayList<>();
        allPath.addAll(KSUtil.videoPathList);


        header = (RelativeLayout) findViewById(R.id.topbar);
        tabLay = (LinearLayout) findViewById(R.id.tabLay);
        footer = (RelativeLayout) findViewById(R.id.footer);

        startFrmRecycle = (RecyclerView) findViewById(R.id.rvstartframerecycle);
        endFrmRecycle = (RecyclerView) findViewById(R.id.rvendframerecycle);

        back = (ImageView) findViewById(R.id.back);
        done = (ImageView) findViewById(R.id.done);
        startIV = (ImageView) findViewById(R.id.startiv);
        endIV = (ImageView) findViewById(R.id.endiv);


        startframebtn = (ImageView) findViewById(R.id.startframebtn);
        endframebtn = (ImageView) findViewById(R.id.endframebtn);


        arrayListmain = application.getSelectedImages();

        posstart = 0;


        sFrmAdapter = new StartFrameAdapter();
        eFrmAdapter = new EndFrameAdapter();

        hrlManagaer = new MyCustomLayoutManager(getApplicationContext());
        startFrmRecycle.setLayoutManager(hrlManagaer);

        hrlManagaer2 = new MyCustomLayoutManager(getApplicationContext());
        endFrmRecycle.setLayoutManager(hrlManagaer2);

        startFrmRecycle.setAdapter(sFrmAdapter);
        endFrmRecycle.setAdapter(eFrmAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        startIV.setVisibility(View.VISIBLE);
        startFrmRecycle.setVisibility(View.VISIBLE);

        firstBit = application.loadBitmapFromAssets(getApplicationContext(), "startframe/" +
                KessiApplication.startframelist[0]);
        startIV.setImageBitmap(firstBit);


        endBit = application.loadBitmapFromAssets(getApplicationContext(), "endframe/"
                + KessiApplication.endframelist[0]);
        endIV.setImageBitmap(endBit);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(v);
            }
        });

        startframebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endFrmRecycle.setVisibility(View.GONE);
                startIV.setVisibility(View.VISIBLE);
                firstBit = application.loadBitmapFromAssets(getApplicationContext(), "startframe/" +
                        KessiApplication.startframelist[posstart]);
                startIV.setImageBitmap(firstBit);
                endIV.setVisibility(View.GONE);
                if (startFrmRecycle.getVisibility() == View.VISIBLE) {
                    startFrmRecycle.setVisibility(View.GONE);
                    startframebtn.setImageResource(R.drawable.start_unpress);

                } else {
                    startFrmRecycle.setVisibility(View.VISIBLE);
                    startframebtn.setImageResource(R.drawable.start_press);
                    endframebtn.setImageResource(R.drawable.end_unpress);
                }
            }
        });

        endframebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFrmRecycle.setVisibility(View.GONE);
                startIV.setVisibility(View.GONE);
                endBit = application.loadBitmapFromAssets(getApplicationContext(), "endframe/"
                        + KessiApplication.endframelist[posstart]);
                endIV.setImageBitmap(endBit);
                endIV.setVisibility(View.VISIBLE);
                if (endFrmRecycle.getVisibility() == View.VISIBLE) {
                    endFrmRecycle.setVisibility(View.GONE);
                    endframebtn.setImageResource(R.drawable.end_unpress);

                } else {
                    endFrmRecycle.setVisibility(View.VISIBLE);
                    endframebtn.setImageResource(R.drawable.end_press);
                    startframebtn.setImageResource(R.drawable.start_unpress);

                }
            }
        });


        setLTV();
    }

//    public void setTV(){
//        LinearLayout adContainer = findViewById(R.id.banner_container);
//        if (!AdAdmob.isloadFbMAXAd) {
//            //admob
//            AdAdmob.initAd(DecorFrmActivity.this);
//            AdAdmob.loadBannerAd(DecorFrmActivity.this, adContainer);
//            AdAdmob.loadInterAd(DecorFrmActivity.this);
//        } else {
//            //MAX + Fb banner Ads
////            AdManager.initMAX(DecorFrmActivity.this);
//            AdAdmob.maxInterstital(DecorFrmActivity.this);
//        }
//    }

//
//    void startActivityes(Intent intent, int reqCode) {
//        if (!AdAdmob.isloadFbMAXAd) {
//            AdAdmob.adCounter++;
//            AdAdmob.showInterAd(DecorFrmActivity.this, intent, reqCode);
//        } else {
//            AdAdmob.adCounter++;
//            AdAdmob.showMaxInterstitial(DecorFrmActivity.this, intent, reqCode);
//        }
//    }

    public ImageData getItem(int pos) {
        ArrayList<ImageData> list = application.getSelectedImagesstart();
        if (list.size() <= pos) {
            return new ImageData();
        }
        return (ImageData) list.get(pos);
    }


    class StartFrameAdapter extends RecyclerView.Adapter<StartFrameAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            RelativeLayout relend;

            public MyViewHolder(View view) {
                super(view);
                this.imageView = (ImageView) view.findViewById(R.id.framerow_img);
                this.relend = (RelativeLayout) view.findViewById(R.id.framerow_bg);
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels * 266 / 1080,
                        getResources().getDisplayMetrics().widthPixels * 266 / 1080));
                relend.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels * 266 / 1080,
                        getResources().getDisplayMetrics().widthPixels * 266 / 1080));
            }
        }

        StartFrameAdapter() {
        }

        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.framerow_item,
                    viewGroup, false));
        }

        public void onBindViewHolder(MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {

            final ImageData item = getItem(i);
            InputStream open;
            try {
                AssetManager assets = getAssets();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("startframe/");
                stringBuilder.append(KessiApplication.startframelist[i]);
                open = assets.open(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
                open = null;
            }

            myViewHolder.imageView.setImageBitmap(BitmapFactory.decodeStream(open));
            myViewHolder.relend.setBackgroundColor(ContextCompat.getColor(DecorFrmActivity.this,
                    R.color.colorwhite));

            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    try {

                        firstBit = application.loadBitmapFromAssets(getApplicationContext(), "startframe/" +
                                KessiApplication.startframelist[i]);
                        posstart = i;
//                        posend = posstart;
                        startIV.setImageBitmap(firstBit);


                    } catch (Exception e) {

                    }
                }
            });
        }

        public int getItemCount() {
            return KessiApplication.startframelist.length;
        }

    }

    class EndFrameAdapter extends RecyclerView.Adapter<EndFrameAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            RelativeLayout relend;

            public MyViewHolder(View view) {
                super(view);
                this.imageView = (ImageView) view.findViewById(R.id.framerow_img);
                this.relend = (RelativeLayout) view.findViewById(R.id.framerow_bg);
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels * 266 / 1080,
                        getResources().getDisplayMetrics().widthPixels * 266 / 1080));
                relend.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels * 266 / 1080,
                        getResources().getDisplayMetrics().widthPixels * 266 / 1080));
            }
        }

        EndFrameAdapter() {


        }

        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.framerow_item, viewGroup, false));
        }

        public void onBindViewHolder(MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {
            InputStream open;
            try {
                AssetManager assets = getAssets();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("endframe/");
                stringBuilder.append(KessiApplication.endframelist[i]);
                open = assets.open(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
                open = null;
            }

            myViewHolder.imageView.setImageBitmap(BitmapFactory.decodeStream(open));
            myViewHolder.relend.setBackgroundColor(ContextCompat.getColor(DecorFrmActivity.this,
                    R.color.colorwhite));

            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    try {
                        endBit = application.loadBitmapFromAssets(getApplicationContext(), "endframe/"
                                + KessiApplication.endframelist[i]);


                        posstart = i;
                        endIV.setImageBitmap(endBit);


                    } catch (Exception e) {

                    }
                }
            });
        }

        public int getItemCount() {
            return KessiApplication.endframelist.length;
        }

    }


    public class MyCustomLayoutManager extends LinearLayoutManager {
        private final float MILLISECONDS_PER_INCH = 350.0f;
        private Context mContext;

        public MyCustomLayoutManager(Context context) {
            super(context);
            mContext = context;
        }

        public void setOrientation(int i) {
            super.setOrientation(RecyclerView.HORIZONTAL);
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {

            LinearSmoothScroller recyclerView1 = new LinearSmoothScroller(mContext) {
                public PointF computeScrollVectorForPosition(int i) {
                    return computeScrollVectorForPosition(i);
                }

                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return (350.0f / ((float) displayMetrics.densityDpi));
                }
            };
            recyclerView1.setTargetPosition(i);
        }
    }

    public void open(View view) {

        final Dialog d_delete = new Dialog(DecorFrmActivity.this);
        d_delete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d_delete.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

        d_delete.setContentView(R.layout.dialog_delete);

        TextView maintext = (TextView) d_delete.findViewById(R.id.maintext);
        img_btn_yes = (ImageView) d_delete.findViewById(R.id.img_btn_yes);
        img_btn_no = (ImageView) d_delete.findViewById(R.id.img_btn_no);
        maindailog = (LinearLayout) d_delete.findViewById(R.id.maindailog);


        popuplayout_dialog();

        maintext.setText(getResources().getString(R.string.back_msg));
        img_btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                startFrameBitmap = application.loadBitmapFromAssets(getApplicationContext(),
                        "startframe/" + application.startframelist[posstart]);


                endFrameBitmap = application.loadBitmapFromAssets(getApplicationContext(),
                        "endframe/" + application.endframelist[posstart]);

                new SaveAsync().execute();
                d_delete.dismiss();
            }
        });
        img_btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                application.isEditEnable = false;

                startActivityForResult(new Intent(DecorFrmActivity.this, VideoThemeActivity.class),0);

                d_delete.dismiss();
            }
        });
        d_delete.show();

    }

    File resultingfile;

    String startFramePath, endFramePath;

    class SaveAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(DecorFrmActivity.this);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            startFramePath = saver(startFrameBitmap);
            Log.e("startFramePath", startFramePath);
            endFramePath = saver(endFrameBitmap);
            Log.e("endFramePath", endFramePath);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            KSUtil.videoPathList.clear();
            KSUtil.videoPathList.add(startFramePath);


            for (String str : allPath) {
                KSUtil.videoPathList.add(str);
                Log.e("allPath", str);
            }

            KSUtil.videoPathList.add(endFramePath);

            application.isEditEnable = false;
            application.selectedImages.clear();

            for (int i = 0; i < KSUtil.videoPathList.size(); i++) {
                ImageData idata = new ImageData();
                idata.setImagePath(KSUtil.videoPathList.get(i));
                application.selectedImages.add(i, idata);
            }
            startActivityForResult(new Intent(DecorFrmActivity.this, VideoThemeActivity.class),0);
        }
    }


    public String saver(Bitmap bitmap) {
        String folder = getResources().getString(R.string.app_name);
        try {
            File rootFile = new File(Environment.getExternalStorageDirectory()
                    .toString() + "/" + folder + "/editor");
            rootFile.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-" + n + ".jpg";

            resultingfile = new File(rootFile, fname);

            if (resultingfile.exists())
                resultingfile.delete();
            try {
                FileOutputStream Fout = new FileOutputStream(resultingfile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, Fout);
                Fout.flush();
                Fout.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
        return resultingfile.getAbsolutePath();
    }


    void setLTV() {

        RelativeLayout.LayoutParams pBtn = LayManager.RelParams(DecorFrmActivity.this,90,90);
        pBtn.addRule(RelativeLayout.CENTER_IN_PARENT);
        back.setLayoutParams(pBtn);
        done.setLayoutParams(pBtn);

        RelativeLayout.LayoutParams pHeader = LayManager.RelParams(DecorFrmActivity.this,1080,168);
        header.setLayoutParams(pHeader);


        LinearLayout.LayoutParams paramsTab = LayManager.LinParams(DecorFrmActivity.this,1080,82);
        tabLay.setLayoutParams(paramsTab);


        RelativeLayout.LayoutParams pFooter = LayManager.RelParams(DecorFrmActivity.this,1080,340);
        footer.setLayoutParams(pFooter);

    }

    void popuplayout_dialog() {

        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 270 / 1080,
                getResources().getDisplayMetrics().heightPixels * 106 / 1920);
        params4.addRule(RelativeLayout.CENTER_IN_PARENT);
        img_btn_yes.setLayoutParams(params4);
        img_btn_no.setLayoutParams(params4);

        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 840 / 1080,
                getResources().getDisplayMetrics().heightPixels * 415 / 1920);
        params5.addRule(RelativeLayout.CENTER_IN_PARENT);
        maindailog.setLayoutParams(params5);
    }

}
