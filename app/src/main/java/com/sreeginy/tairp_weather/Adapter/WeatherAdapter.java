package com.sreeginy.tairp_weather.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sreeginy.tairp_weather.Model.ForecastWeatherData;
import com.sreeginy.tairp_weather.R;
//import com.sreeginy.weather.Model.ForecastWeatherData;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {


    private List<ForecastWeatherData> forecastDataList;

    public WeatherAdapter(List<ForecastWeatherData> forecastDataList) {
        this.forecastDataList = forecastDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastWeatherData forecastData = forecastDataList.get(position);

        holder.dateTextView.setText(forecastData.getDate());

//        holder.temperatureTextView.setText(forecastData.getTemperature());


        holder.temperatureTextView.setText(formatTemperature(forecastData.getTemperature()));

        holder.weatherType.setText(formatWeatherType(forecastData.getWeatherType()));


    }

    private String formatWeatherType(String weatherType) {
        // Perform any formatting or modification to the weather type as needed
        // For example, you can add a prefix or modify the text according to your requirements.
        String formattedWeatherType = "天气 :  " + weatherType;

        return formattedWeatherType;
    }


    // Add the formatTemperature method to the Adapter class
    private String formatTemperature(String temperature) {
        int roundedTemp = Integer.parseInt(temperature);
        return "温度 : " + roundedTemp + "°";
    }

    @Override
    public int getItemCount() {
        return forecastDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView temperatureTextView;
        public TextView weatherType;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            weatherType = itemView.findViewById(R.id.weatherType);
        }
    }
}
