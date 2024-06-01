package com.sreeginy.tairp_weather.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sreeginy.tairp_weather.R;
import com.sreeginy.tairp_weather.Model.WeatherData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE1 = 1;
    TextView date;
    String apiKey = "0834e2dbfe812be60ce5bb46a74aa17c";
    String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    String apiUrl;

    String lat;
    String lng;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    String locationProvider = LocationManager.GPS_PROVIDER;

    TextView mNameOfCity, mWeatherType, mTemperature, rain, windSpeed, humidity;
    ImageView mWeatherIcon;
    Button searchBtn;
    Button scheduleBtn;
    Button mp3Btn;
    Button currentLocationBtn;
    LocationManager locationManager;
    LocationListener locationListener;
    ImageButton viewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCity("沈阳市");

        mWeatherType = findViewById(R.id.weatherType);
        mTemperature = findViewById(R.id.temperature);
        mWeatherIcon = findViewById(R.id.weatherIcon);
        searchBtn = findViewById(R.id.searchBtn);
        mNameOfCity = findViewById(R.id.cityName);
        date = findViewById(R.id.date);

        rain = findViewById(R.id.rain);
        windSpeed = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);

        viewBtn = findViewById(R.id.viewBtn);
        mp3Btn = findViewById(R.id.mp3Btn);
        scheduleBtn = findViewById(R.id.scheduleBtn);


        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        String currentDate = format.format(new Date());
        date.setText(currentDate);

        // 在 onCreate 方法中或其他合适的地方找到按钮引用并设置点击监听器
        currentLocationBtn = findViewById(R.id.getCurrentLocationBtn);
        currentLocationBtn.setOnClickListener( v -> {
            String latitude = getLat();
            String longitude = getLng();
            RequestParams params = new RequestParams();
            params.put("lat", latitude);
            params.put("lon", longitude);
            params.put("appid", apiKey);
            apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
            letsdoSomeNetworking(params);
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, REQUEST_CODE1);
            }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeatherSortingActivity.class);
                startActivity(intent);
            }
        });

        mWeatherIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WeatherData weatherData = new WeatherData();
                weatherData.setmNameOfCity(mNameOfCity.getText().toString());
                weatherData.setmTemperature(mTemperature.getText().toString());
                weatherData.setmWeatherType(mWeatherType.getText().toString());
                weatherData.setmWeatherIcon("weather_icon"); // Set the appropriate weather icon value
                weatherData.setRain(rain.getText().toString());
                weatherData.setWindSpeed(windSpeed.getText().toString());
                weatherData.setHumidity(humidity.getText().toString());



                Intent intent = new Intent(MainActivity.this, ViewWeatherDetails.class);
                intent.putExtra("weatherData", weatherData);
                startActivity(intent);
            }
        });
        mp3Btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MediaActivity.class);
            startActivity(intent);
        });
       scheduleBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CourseActivity.class);
            startActivity(intent);
        });
        apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
        getWeatherForCurrentLocation();
    }


    private void getWeatherForNewCity(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", apiKey);
        letsdoSomeNetworking(params);
    }

    private void getWeatherForCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                setLat(latitude);
                setLng(longitude);
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", apiKey);
                letsdoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }
        };


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        locationManager.requestLocationUpdates(locationProvider, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Location obtained successfully", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            } else {
                Toast.makeText(MainActivity.this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void letsdoSomeNetworking(RequestParams params) {
        // 创建一个异步HTTP客户端
        AsyncHttpClient client = new AsyncHttpClient();
        // 发送GET请求，并传入参数
        client.get(apiUrl, params, new JsonHttpResponseHandler() {
            // 请求成功时的回调
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // 处理响应数据
                Toast.makeText(MainActivity.this, "数据加载成功", Toast.LENGTH_SHORT).show();
                WeatherData weatherData = null;
                try {
                    weatherData = WeatherData.fromJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                updateUI(weatherData);

            }
        });
    }
    public void updateUI(WeatherData weather) {
        mTemperature.setText(weather.getmTemperature() + "°");
        mNameOfCity.setText(weather.getmNameOfCity());
        mWeatherType.setText("Mostly " + weather.getmWeatherType());
        int resourceId = getResources().getIdentifier(weather.getmWeatherIcon(), "drawable", getPackageName());
        mWeatherIcon.setImageResource(resourceId);
        rain.setText(weather.getRain() + " mm");
        windSpeed.setText(weather.getWindSpeed() + " km/h");
        humidity.setText(weather.getHumidity() + "%");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE1){
            if (resultCode == RESULT_OK && data != null){
                 String backcity = data.getStringExtra("City");
                if (city != null) {
                    apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + backcity + "&appid=" + apiKey;
                    getWeatherForNewCity(backcity);
                } else {
                    getWeatherForCurrentLocation();
                }
            }
        }
    }
}
