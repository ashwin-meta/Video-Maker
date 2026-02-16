package com.photofusion.holi.videomaker.photo.slideshow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.fire.initialcheck.internal.InitialCheck;
import com.photofusion.holi.videomaker.photo.slideshow.util.AdAdmob;
import com.photofusion.holi.videomaker.photo.slideshow.util.BaseActivity;
import com.photofusion.holi.videomaker.photo.slideshow.util.JsonParserTask;
import com.photofusion.holi.videomaker.photo.slideshow.util.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity implements JsonParserTask.OnJsonParseCompleteListener{

    ImageView icon, bgIV;


   public static List<String> adIdsis = new ArrayList<>();
    private static final String JSON_URL = "https://appcodezilla.com/Category.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        adIdsis.add(getString(R.string.admob_interstitial));
        adIdsis.add(getString(R.string.admob_interstitial1));
        adIdsis.add(getString(R.string.admob_interstitial2));


        JsonParserTask jsonParserTask = new JsonParserTask(this);
        jsonParserTask.execute(JSON_URL);

        com.facebook.ads.AdSettings.addTestDevice("4833be53-293f-44e0-a711-7906db745ad4");
        InitialCheck.init(KessiApplication.global_ctx, "https://pfadmin.metacoderz.com/");

        icon = findViewById(R.id.icon);
        bgIV = findViewById(R.id.bgIV);






    }


    public void onJsonParseComplete(String json) {
        if (json != null) {
            try {
                // Parse the JSON data
                JSONObject jsonObject = new JSONObject(json);

                // Access the "Category" object
                JSONObject categoryObject = jsonObject.getJSONObject("Category");

                // Access the "Photoeditor" array
                JSONArray photoeditorArray = categoryObject.getJSONArray("Videoeditor");

                // Check if the array is not empty
                if (photoeditorArray.length() > 0) {
                    // Get the first object from the array
                    JSONObject appDataObject = photoeditorArray.getJSONObject(0);

                    // Access the "InHouseAdPlaystorelink" value
                    String inHouseAdPlaystoreLink = appDataObject.getString("InHouseAdPlaystorelink");

                    if (inHouseAdPlaystoreLink.contains("com.kessiinfo.testapp"))
                    {
                        json2(json);
                        return;
                    }

                    Log.e("JSON", "InHouseAdPlaystoreLink: " + inHouseAdPlaystoreLink);
                    StartActivity.DownloadUri = inHouseAdPlaystoreLink;

                    // Get the second object from the array (for "InHouseAdImageUrl")
                    JSONObject imageObject = photoeditorArray.getJSONObject(1);

                    // Access the "InHouseAdImageUrl" value
                    String inHouseAdImageUrl = imageObject.getString("InHouseAdImageUrl");
                    Log.e("JSON", "InHouseAdImageUrl: " + inHouseAdImageUrl);
                    StartActivity.ImageUri = inHouseAdImageUrl;

                    // Do something with the extracted values
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public  void json2(String json)
    {
        if (json != null) {
            try {
                // Parse the JSON data
                JSONObject jsonObject = new JSONObject(json);

                // Access the "Category" object
                JSONObject categoryObject = jsonObject.getJSONObject("Category");

                // Access the "Photoeditor" array
                JSONArray photoeditorArray = categoryObject.getJSONArray("Photoframe");

                // Check if the array is not empty
                if (photoeditorArray.length() > 0) {
                    // Get the first object from the array
                    JSONObject appDataObject = photoeditorArray.getJSONObject(0);

                    // Access the "InHouseAdPlaystorelink" value
                    String inHouseAdPlaystoreLink = appDataObject.getString("InHouseAdPlaystorelink");

                    Log.e("JSON11", "InHouseAdPlaystoreLink: " + inHouseAdPlaystoreLink);
                    StartActivity.DownloadUri = inHouseAdPlaystoreLink;

                    // Get the second object from the array (for "InHouseAdImageUrl")
                    JSONObject imageObject = photoeditorArray.getJSONObject(1);

                    // Access the "InHouseAdImageUrl" value
                    String inHouseAdImageUrl = imageObject.getString("InHouseAdImageUrl");
                    Log.e("JSON11", "InHouseAdImageUrl: " + inHouseAdImageUrl);
                    StartActivity.ImageUri = inHouseAdImageUrl;

                    // Do something with the extracted values
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        InitialCheck.versionCheck(3000, i -> {
            Log.e( "onCreate111s: ", InitialCheck.REMOTE_AD);
            if (InitialCheck.REMOTE_AD.equals("a"))
            {
                SharedPrefs.setREMOTE_AD(this,"a");
            }
            else if(InitialCheck.REMOTE_AD.equals("f"))
            {
                SharedPrefs.setREMOTE_AD(this,"f");
            }
            startActivity(new Intent(SplashActivity.this, StartActivity.class));
            finish();
        });


    }

}
