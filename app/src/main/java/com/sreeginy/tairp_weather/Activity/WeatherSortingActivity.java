package com.sreeginy.tairp_weather.Activity;

import static android.R.layout.simple_spinner_dropdown_item;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sreeginy.tairp_weather.Adapter.WeatherAdapter;
import com.sreeginy.tairp_weather.Model.ForecastWeatherData;
import com.sreeginy.tairp_weather.R;
import com.sreeginy.tairp_weather.WeatherHttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WeatherSortingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private List<ForecastWeatherData> forecastDataList;
    private Spinner sortSpinner;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_sorting);

        backBtn = findViewById(R.id.backBtn);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        forecastDataList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(forecastDataList);
        recyclerView.setAdapter(weatherAdapter);

        sortSpinner = findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        fetchData();
    }

    private void fetchData() {
        WeatherHttpClient weatherHttpClient = new WeatherHttpClient();
        weatherHttpClient.fetch7DayForecastData("Northern Province", new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WeatherHttpClient.FORECAST_FETCH_SUCCESS) {
                    ArrayList<ForecastWeatherData> forecastData = (ArrayList<ForecastWeatherData>) msg.obj;
                    updateData(forecastData);
                } else {
                    Log.e("WeatherSortingActivity", "Failed to fetch forecast data");
                }
            }
        });
    }
    private void updateData(ArrayList<ForecastWeatherData> forecastData) {
        forecastDataList.clear();
        forecastDataList.addAll(forecastData);
        Collections.sort(forecastDataList, new Comparator<ForecastWeatherData>() {
            @Override
            public int compare(ForecastWeatherData data1, ForecastWeatherData data2) {
                return data1.getDate().compareTo(data2.getDate());
            }
        });
        weatherAdapter.notifyDataSetChanged();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedOption = parent.getItemAtPosition(position).toString();
        sortData(selectedOption);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    private void sortData(String selectedOption) {
        switch (selectedOption) {

            case "温度":
                // Sort the data based on temperature in ascending order
                Collections.sort(forecastDataList, new Comparator<ForecastWeatherData>() {
                    @Override
                    public int compare(ForecastWeatherData data1, ForecastWeatherData data2) {
                        return data1.getTemperature().compareTo(data2.getTemperature());
                    }
                });
                break;
            case "日期":
                // Sort the data based on the date in ascending order
                Collections.sort(forecastDataList, new Comparator<ForecastWeatherData>() {
                    @Override
                    public int compare(ForecastWeatherData data1, ForecastWeatherData data2) {
                        return data1.getDate().compareTo(data2.getDate());
                    }
                });
                break;
            case "天气类型":
                // Sort the data based on the weather type in ascending order
                Collections.sort(forecastDataList, new Comparator<ForecastWeatherData>() {
                    @Override
                    public int compare(ForecastWeatherData data1, ForecastWeatherData data2) {
                        return data1.getWeatherType().compareTo(data2.getWeatherType());
                    }
                });
                break;
            default:
                // Default sorting: by date in ascending order
                Collections.sort(forecastDataList, new Comparator<ForecastWeatherData>() {
                    @Override
                    public int compare(ForecastWeatherData data1, ForecastWeatherData data2) {
                        return data1.getDate().compareTo(data2.getDate());
                    }
                });
                break;
        }

        weatherAdapter.notifyDataSetChanged();
    }
}
