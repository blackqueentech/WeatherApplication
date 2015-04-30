package com.example.budapest.weatherapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.budapest.weatherapplication.data.WeatherForecast;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    private final String URL_BASE =
            "http://api.openweathermap.org/data/2.5/weather?q=";


    @InjectView(R.id.etCity)
    EditText etCity;
    @InjectView(R.id.tvTemp)
    TextView tvTemp;
    @InjectView(R.id.tvMinTemp)
    TextView tvMinTemp;
    @InjectView(R.id.tvMaxTemp)
    TextView tvMaxTemp;
    @InjectView(R.id.tvHumidity)
    TextView tvHumidity;
    @InjectView(R.id.tvDesc)
    TextView tvDesc;
    @InjectView(R.id.tvSunrise)
    TextView tvSunrise;
    @InjectView(R.id.tvSunset)
    TextView tvSunset;
    @InjectView(R.id.ivIcon)
    ImageView ivIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btnGo)
    public void startButtonHandler(View view) {
        String query = URL_BASE  + etCity.getText() + "&units=imperial";

        new HttpGetTask(getApplicationContext()).
                execute(query);
    }




    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(WeatherForecast weatherForecast) {
        Date sunriseTime = new Date(weatherForecast.getSys().getSunrise());
        Date sunsetTime = new Date(weatherForecast.getSys().getSunset());
        SimpleDateFormat date = new SimpleDateFormat("hh:mm");
        tvTemp.setText("The weather in " + etCity.getText().toString() + " is " +
                weatherForecast.getMain().getTemp().toString() + "\u00B0F");
        tvMinTemp.setText("The minimum temperature is " + weatherForecast.getMain().getTempMin().toString() + "\u00B0F");
        tvMaxTemp.setText("The maximum temperature is " + weatherForecast.getMain().getTempMax().toString() + "\u00B0F");
        tvHumidity.setText("The humidity is "+ weatherForecast.getMain().getHumidity().toString());
        tvSunrise.setText(date.format(sunriseTime));
        tvSunset.setText(date.format(sunsetTime));
        tvDesc.setText(weatherForecast.getWeather().get(0).getDescription());

        String iconId = weatherForecast.getWeather().get(0).getIcon();
        Glide.with(getApplicationContext()).load(
                "http://openweathermap.org/img/w/"
                        + iconId
                        + ".png").into(ivIcon);
    }
    private BroadcastReceiver brWeatherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String rawResult = intent.getStringExtra(
                    HttpGetTask.KEY_RESULT);

            try {

                Gson gson = new Gson();
                WeatherForecast weatherForecast = gson.fromJson(rawResult,
                        WeatherForecast.class);

                Date sunriseTime = new Date(weatherForecast.getSys().getSunrise());
                Date sunsetTime = new Date(weatherForecast.getSys().getSunset());
                SimpleDateFormat date = new SimpleDateFormat("hh:mm");

                tvTemp.setText("The weather in " + etCity.getText().toString() + " is " +
                        weatherForecast.getMain().getTemp().toString() + "\u00B0F");
                tvMinTemp.setText("The minimum temperature is " + weatherForecast.getMain().getTempMin().toString() + "\u00B0F");
                tvMaxTemp.setText("The maximum temperature is " + weatherForecast.getMain().getTempMax().toString() + "\u00B0F");
                tvHumidity.setText("The humidity is "+ weatherForecast.getMain().getHumidity().toString());
                tvSunrise.setText(date.format(sunriseTime));
                tvSunset.setText(date.format(sunsetTime));
                String iconId = weatherForecast.getWeather().get(0).getIcon();
                Glide.with(getApplicationContext()).load(
                        "http://openweathermap.org/img/w/"
                        + iconId
                        + ".png").into(ivIcon);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


}
