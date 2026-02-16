package com.photofusion.holi.videomaker.photo.slideshow.util;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonParserTask extends AsyncTask<String, Void, String> {

    private OnJsonParseCompleteListener listener;

    public interface OnJsonParseCompleteListener {
        void onJsonParseComplete(String json);
    }

    public JsonParserTask(OnJsonParseCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            connection.disconnect();

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onJsonParseComplete(result);
        }
    }
}
