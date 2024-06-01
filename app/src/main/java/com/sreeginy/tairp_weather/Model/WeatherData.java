package com.sreeginy.tairp_weather.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData implements Parcelable {
    private String mNameOfCity;
    private String mTemperature;
    private String mWeatherType;
    private String mWeatherIcon;
    private String rain;
    private String windSpeed;
    private String humidity;
    private int condition;
    private double latitude;
    private double longitude;
    private long sunrise;
    private long sunset;
    private int pressure;
    private double maxTemperature;
    private double minTemperature;

    public WeatherData() {
        // Default constructor
    }

    protected WeatherData(Parcel in) {
        mNameOfCity = in.readString();
        mTemperature = in.readString();
        mWeatherType = in.readString();
        mWeatherIcon = in.readString();
        rain = in.readString();
        windSpeed = in.readString();
        humidity = in.readString();
        condition = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        sunrise = in.readLong();
        sunset = in.readLong();
        pressure = in.readInt();
        maxTemperature = in.readDouble();
        minTemperature = in.readDouble();
    }

    public static final Creator<WeatherData> CREATOR = new Creator<WeatherData>() {
        @Override
        public WeatherData createFromParcel(Parcel in) {
            return new WeatherData(in);
        }

        @Override
        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };

    public static WeatherData fromJson(JSONObject jsonObject) throws JSONException {
        try {
            WeatherData weatherData = new WeatherData();

            weatherData.mNameOfCity = jsonObject.getString("name");
            weatherData.condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.mWeatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weatherData.mWeatherIcon = updateWeatherIcon(weatherData.condition);

            double temperature = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            int roundedValue = (int) Math.rint(temperature);
            weatherData.mTemperature = Integer.toString(roundedValue);

            JSONObject rainObject = jsonObject.optJSONObject("rain");
            weatherData.rain = rainObject != null ? rainObject.getString("3h") : "0";
            weatherData.windSpeed = jsonObject.getJSONObject("wind").getString("speed");
            weatherData.humidity = jsonObject.getJSONObject("main").getString("humidity");

            weatherData.setLongitude(jsonObject.getJSONObject("coord").getDouble("lon"));
            weatherData.setSunrise(jsonObject.getJSONObject("sys").getLong("sunrise"));
            weatherData.setSunset(jsonObject.getJSONObject("sys").getLong("sunset"));
            weatherData.setPressure(jsonObject.getJSONObject("main").getInt("pressure"));

            double maxTemperature = jsonObject.getJSONObject("main").getDouble("temp_max");
            weatherData.setMaxTemperature(maxTemperature);

            double minTemperature = jsonObject.getJSONObject("main").getDouble("temp_min");
            weatherData.setMinTemperature(minTemperature);

            weatherData.setLatitude(jsonObject.getJSONObject("coord").getDouble("lat"));

            return weatherData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

//    private static String formatTemperature(double temperature) {
//        int roundedValue = (int) Math.rint(temperature);
//        return Integer.toString(roundedValue) + "°";
//    }


    private static String updateWeatherIcon(int condition) {
        if (condition >= 0 && condition <= 300) {
            return "thunderstorm1";
        } else if (condition >= 300 && condition <= 500) {
            return "lightrain";
        } else if (condition >= 500 && condition <= 600) {
            return "shower";
        } else if (condition >= 600 && condition <= 700) {
            return "snow";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition <= 800) {
            return "overcast";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy";
        } else if (condition >= 900 && condition <= 902) {
            return "thunderstorm";
        } else if (condition == 903) {
            return "snow1";
        } else if (condition == 904) {
            return "clear";
        } else if (condition >= 905 && condition <= 1000) {
            return "thunderstorm2";
        } else {
            return "clear";
        }
    }

    public String getmNameOfCity() {
        return mNameOfCity;
    }

    public String getmTemperature() {
        return mTemperature;
    }

    public String getmWeatherType() {
        return mWeatherType;
    }

    public String getmWeatherIcon() {
        return mWeatherIcon;
    }

    public String getRain() {
        return rain;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

    public int getCondition() {
        return condition;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public int getPressure() {
        return pressure;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public void setmNameOfCity(String mNameOfCity) {
        this.mNameOfCity = mNameOfCity;
    }

    public void setmTemperature(String mTemperature) {
        this.mTemperature = mTemperature;
    }

    public void setmWeatherType(String mWeatherType) {
        this.mWeatherType = mWeatherType;
    }

    public void setmWeatherIcon(String mWeatherIcon) {
        this.mWeatherIcon = mWeatherIcon;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mNameOfCity);
        dest.writeString(mTemperature);
        dest.writeString(mWeatherType);
        dest.writeString(mWeatherIcon);
        dest.writeString(rain);
        dest.writeString(windSpeed);
        dest.writeString(humidity);
        dest.writeInt(condition);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeLong(sunrise);
        dest.writeLong(sunset);
        dest.writeInt(pressure);
        dest.writeDouble(maxTemperature);
        dest.writeDouble(minTemperature);
    }


  
}
