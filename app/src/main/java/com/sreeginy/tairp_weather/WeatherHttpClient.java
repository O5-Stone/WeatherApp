package com.sreeginy.tairp_weather;

import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sreeginy.tairp_weather.Model.ForecastWeatherData;
import com.sreeginy.tairp_weather.Model.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class WeatherHttpClient {


    private static final String TAG = WeatherHttpClient.class.getSimpleName();

    public static final int WEATHER_FETCH_SUCCESS = 1;
    public static final int WEATHER_FETCH_FAILURE = 2;

    public static final int FORECAST_FETCH_SUCCESS = 3;
    public static final int FORECAST_FETCH_FAILURE = 4;

    private static final String API_KEY = "0834e2dbfe812be60ce5bb46a74aa17c";
    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast";

    public void fetchWeatherData(String cityName, final Handler weatherHandler) {
        String apiUrl = API_BASE_URL + "?q=" + cityName + "&appid=" + API_KEY;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                        bufferedReader.close();
                        inputStream.close();

                        WeatherData weatherData = parseWeatherData(response.toString());
                        if (weatherData != null) {
                            Message successMessage = weatherHandler.obtainMessage(WEATHER_FETCH_SUCCESS, weatherData);
                            successMessage.sendToTarget();
                        } else {
                            Log.e(TAG, "Failed to parse weather data");
                            weatherHandler.sendEmptyMessage(WEATHER_FETCH_FAILURE);
                        }
                    } else {
                        Log.e(TAG, "Weather API request failed with response code: " + responseCode);
                        weatherHandler.sendEmptyMessage(WEATHER_FETCH_FAILURE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    weatherHandler.sendEmptyMessage(WEATHER_FETCH_FAILURE);
                }
            }
        }).start();
    }

    private WeatherData parseWeatherData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return WeatherData.fromJson(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    private ArrayList<ForecastWeatherData> parseForecastData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray forecastList = jsonObject.getJSONArray("list");

            ArrayList<ForecastWeatherData> forecastDataList = new ArrayList<>();

            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject forecastObject = forecastList.getJSONObject(i);
                ForecastWeatherData forecastData = ForecastWeatherData.fromJson(forecastObject);
                forecastDataList.add(forecastData);
            }

            return forecastDataList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



    public void fetch5DayForecastData(String cityName, final Handler forecastHandler) {
        String apiUrl = API_FORECAST_URL + "?q=" + cityName + "&appid=" + API_KEY;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                        bufferedReader.close();
                        inputStream.close();

                        ArrayList<ForecastWeatherData> forecastData = parse5DayForecastData(response.toString());
                        if (forecastData != null) {
                            Message successMessage = forecastHandler.obtainMessage(FORECAST_FETCH_SUCCESS, forecastData);
                            successMessage.sendToTarget();
                        } else {
                            Log.e(TAG, "Failed to parse forecast data");
                            forecastHandler.sendEmptyMessage(FORECAST_FETCH_FAILURE);
                        }
                    } else {
                        Log.e(TAG, "Forecast API request failed with response code: " + responseCode);
                        forecastHandler.sendEmptyMessage(FORECAST_FETCH_FAILURE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    forecastHandler.sendEmptyMessage(FORECAST_FETCH_FAILURE);
                }
            }
        }).start();
    }

    private ArrayList<ForecastWeatherData> parse5DayForecastData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray forecastList = jsonObject.getJSONArray("list");
            ArrayList<ForecastWeatherData> forecastDataList = new ArrayList<>();
            Set<String> processedDays = new HashSet<>(); // Set to keep track of processed days

            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject forecastObject = forecastList.getJSONObject(i);
                String dateTime = forecastObject.getString("dt_txt");
                String day = getFormattedDay(dateTime);

                if (processedDays.contains(day)) {
                    continue;
                }
                processedDays.add(day);
                String icon = forecastObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String weatherType = forecastObject.getJSONArray("weather").getJSONObject(0).getString("main");
                double highTemp = forecastObject.getJSONObject("main").getDouble("temp_max") - 273.15;
                double lowTemp = forecastObject.getJSONObject("main").getDouble("temp_min") - 273.15;

                ForecastWeatherData forecastData = new ForecastWeatherData(day, icon, weatherType, highTemp, lowTemp);
                forecastDataList.add(forecastData);
            }
            return forecastDataList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }




    private String getFormattedDay(String dateTime) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE");
            Date date = inputDateFormat.parse(dateTime);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void fetch7DayForecastData(String cityName, final Handler forecastHandler) {
        String apiUrl = API_FORECAST_URL + "?q=" + cityName + "&appid=" + API_KEY + "&cnt=7";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                        bufferedReader.close();
                        inputStream.close();

                        ArrayList<ForecastWeatherData> forecastData = parse7DayForecastData(response.toString());
                        if (forecastData != null) {
                            Message successMessage = forecastHandler.obtainMessage(FORECAST_FETCH_SUCCESS, forecastData);
                            successMessage.sendToTarget();
                        } else {
                            Log.e(TAG, "Failed to parse forecast data");
                            forecastHandler.sendEmptyMessage(FORECAST_FETCH_FAILURE);
                        }
                    } else {
                        Log.e(TAG, "Forecast API request failed with response code: " + responseCode);
                        forecastHandler.sendEmptyMessage(FORECAST_FETCH_FAILURE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    forecastHandler.sendEmptyMessage(FORECAST_FETCH_FAILURE);
                }
            }
        }).start();
    }

    private ArrayList<ForecastWeatherData> parse7DayForecastData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray forecastList = jsonObject.getJSONArray("list");

            ArrayList<ForecastWeatherData> forecastDataList = new ArrayList<>();

            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject forecastObject = forecastList.getJSONObject(i);
                String date = forecastObject.getString("dt_txt");

                double temperature = forecastObject.getJSONObject("main").getDouble("temp") - 273.15;
                int roundedValue = (int) Math.round(temperature);
                String temperatureString = Integer.toString(roundedValue);

                String weatherType = forecastObject.getJSONArray("weather").getJSONObject(0).getString("main");

                ForecastWeatherData forecastData = new ForecastWeatherData(date, weatherType, temperatureString);
                forecastDataList.add(forecastData);
            }

            return forecastDataList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }





}

