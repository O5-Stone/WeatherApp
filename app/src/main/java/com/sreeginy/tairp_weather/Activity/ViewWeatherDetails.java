package com.sreeginy.tairp_weather.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sreeginy.tairp_weather.R;
import com.sreeginy.tairp_weather.Model.WeatherData;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewWeatherDetails extends AppCompatActivity {

    private TextView temperatureTv, weatherTypeTv, rainTv, windSpeedTv, humidityTv, cityNameTv,latitudeTv, longitudeTv, sunriseTv, sunsetTv, pressureT;
    private ImageButton dayBtn;

    private List<WeatherData> weatherDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_weather_details);
        temperatureTv = findViewById(R.id.temperatureTV);
        weatherTypeTv = findViewById(R.id.weatherTV);
        rainTv = findViewById(R.id.rainTV);
        windSpeedTv = findViewById(R.id.windSpeedTV);
        humidityTv = findViewById(R.id.humidityTV);
        sunriseTv = findViewById(R.id.sunriseTV);
        cityNameTv = findViewById(R.id.cityNameTv);
        dayBtn = findViewById(R.id.sortBtn);

        ImageView backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewWeatherDetails.this, DaysActivity.class);
                startActivity(intent);
            }
        });
        
        updateUI();
    }
    

    public void updateUI() {
        // Get the WeatherData object from the Intent extras
        WeatherData weatherData = getIntent().getParcelableExtra("weatherData");
        if (weatherData != null) {
            temperatureTv.setText(weatherData.getmTemperature());
            weatherTypeTv.setText(weatherData.getmWeatherType());
            rainTv.setText(weatherData.getRain());
            windSpeedTv.setText(weatherData.getWindSpeed());
            humidityTv.setText(weatherData.getHumidity());
            cityNameTv.setText(weatherData.getmNameOfCity());

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            sunriseTv.setText(timeFormat.format(new Date(weatherData.getSunrise() * 1000)));

        }
    }
}
