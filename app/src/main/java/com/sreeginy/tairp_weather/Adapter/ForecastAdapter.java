package com.sreeginy.tairp_weather.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sreeginy.tairp_weather.Model.ForecastWeatherData;
import com.sreeginy.tairp_weather.R;

import java.util.ArrayList;


public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private ArrayList<ForecastWeatherData> items;
    private Context context;

    public ForecastAdapter(ArrayList<ForecastWeatherData> items) {
        this.items = items;
    }

    public void setItems(ArrayList<ForecastWeatherData> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_tommorow, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastWeatherData forecastData = items.get(position);

        holder.dayTxtV.setText(forecastData.getDay());
        holder.weatherType2.setText(forecastData.getWeatherType());

        String highTemp = formatTemperature(forecastData.getHighTemp());
        String lowTemp = formatTemperature2(forecastData.getLowTemp() - 12);
        holder.highTxv.setText(highTemp);
        holder.lowTxV.setText(lowTemp);

        // Load the icon using a library like Glide or Picasso
        String iconUrl = getIconUrl(forecastData.getIcon());
        Glide.with(context)
                .load(iconUrl)
                .into(holder.imageView);
    }

    private String getIconUrl(String iconCode) {
        return "https://openweathermap.org/img/w/" + iconCode + ".png";
    }

    private String formatTemperature(double temperature) {
        int roundedTemp = (int) Math.round(temperature);
        return "最高 :" + roundedTemp + "°";
    }

    private String formatTemperature2(double temperature) {
        int roundedTemp = (int) Math.round(temperature);
        return "最低 :" + roundedTemp + "°";
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTxtV, weatherType2, lowTxV, highTxv;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTxtV = itemView.findViewById(R.id.dayTxtV);
            weatherType2 = itemView.findViewById(R.id.weatherType2);
            lowTxV = itemView.findViewById(R.id.lowTxV);
            highTxv = itemView.findViewById(R.id.highTxv);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
