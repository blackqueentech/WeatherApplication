package com.example.budapest.weatherapplication;

import android.content.Context;
import android.os.AsyncTask;

import com.example.budapest.weatherapplication.data.WeatherForecast;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;

/**
 * Created by Della on 27/04/2015.
 */
public class HttpGetTask extends AsyncTask<String, Void, String> {

    public static final String FILTER_RESULT = "FILTER_RESULT";
    public static final String KEY_RESULT = "KEY_RESULT";
    private Context ctx;

    public HttpGetTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();

                int ch;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((ch = is.read()) != -1) {
                    bos.write(ch);
                }

                result = new String(bos.toByteArray());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        /*Intent intentBrResult = new Intent(FILTER_RESULT);
        intentBrResult.putExtra(KEY_RESULT,result);

        LocalBroadcastManager.getInstance(
            ctx).sendBroadcast(intentBrResult);*/

        try {
            Gson gson = new Gson();
            WeatherForecast weatherForecast = gson.fromJson(result,
                    WeatherForecast.class);

            EventBus.getDefault().post(weatherForecast);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
