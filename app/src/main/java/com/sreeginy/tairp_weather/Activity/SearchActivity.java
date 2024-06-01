package com.sreeginy.tairp_weather.Activity;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sreeginy.tairp_weather.Model.WeatherData;
import com.sreeginy.tairp_weather.R;
import com.sreeginy.tairp_weather.WeatherHttpClient;


public class SearchActivity extends AppCompatActivity {

    private TextView temperature, weatherCon, rain, wind, humidity, cityName, maxTemperature, minTemperature, latitude;
    private ImageView weatherIcon;
    private EditText editText;

    private WeatherHttpClient weatherHttpClient;

    private Handler weatherHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case WeatherHttpClient.WEATHER_FETCH_SUCCESS:
                    WeatherData weatherData = (WeatherData) msg.obj;
                    updateWeatherUI(weatherData);
                    break;
                case WeatherHttpClient.WEATHER_FETCH_FAILURE:
                    Log.e(TAG, "Weather fetch failed");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editText = findViewById(R.id.searchCity);
        ImageView backButton = findViewById(R.id.backBtn);

        temperature = findViewById(R.id.temperature);
        weatherCon = findViewById(R.id.weatherCon);
        rain = findViewById(R.id.rain);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);
        cityName = findViewById(R.id.cityName);
        weatherIcon = findViewById(R.id.weatherIcon);

        maxTemperature = findViewById(R.id.hightemperature);
        minTemperature = findViewById(R.id.lowtemperature);
        latitude = findViewById(R.id.latitude);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCity = editText.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("City", newCity);
                setResult(RESULT_OK, intent);
                finish();
            }
        });



        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String newCity = editText.getText().toString();
                fetchWeatherData(newCity);
                return false;
            }
        });

        weatherHttpClient = new WeatherHttpClient();
    }

    @Override
    public void onBackPressed() {
        String newCity = editText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("City", newCity);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void fetchWeatherData(String cityName) {
        weatherHttpClient.fetchWeatherData(cityName, weatherHandler);
    }

    private void updateWeatherUI(WeatherData weatherData) {
        cityName.setText(weatherData.getmNameOfCity());
        temperature.setText(weatherData.getmTemperature() + "°");
        weatherCon.setText("Mostly " + weatherData.getmWeatherType());
        rain.setText(weatherData.getRain() + " mm");
        wind.setText(weatherData.getWindSpeed() + " km/h");
        humidity.setText(weatherData.getHumidity() + "%");

        // Set weather icon based on the weather condition
        int weatherIconResId = getResources().getIdentifier(
                weatherData.getmWeatherIcon(), "drawable", getPackageName());
        weatherIcon.setImageResource(weatherIconResId);

        maxTemperature.setText(String.format( "%.2f°", weatherData.getMaxTemperature()-273.15));
        minTemperature.setText(String.format("%.2f°", weatherData.getMinTemperature()-285.02));
        latitude.setText(String.format("%.2f°", weatherData.getLatitude()));
    }

}
