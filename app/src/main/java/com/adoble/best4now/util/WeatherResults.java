package com.adoble.best4now.util;

import android.app.Activity;
import android.content.Context;

import com.adoble.best4now.R;
import com.adoble.best4now.domain.Weather;
import com.adoble.best4now.ui.MainActivity;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;


public class WeatherResults {
    // private static final String APP_ID = "ce2ed02687e124b11c4a3a8eb72eb60b"; // OPEN WEATHER MAP KEY

    // https://samples.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=ce2ed02687e124b11c4a3a8eb72eb60b
    // https://samples.openweathermap.org/data/2.5/forecast/daily?lat=35&lon=139&cnt=10&appid=ce2ed02687e124b11c4a3a8eb72eb60b
    private static String basicUrl = "https://samples.openweathermap.org/data/2.5/";
    private static String currentUrl = "weather?";
    private static String dailyURL = "forecast/daily?";

    private Weather currentWeather;
    private ArrayList<Weather> dailyWeather;



    public WeatherResults() {
    }

    public Weather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(Weather currentWeather) {
        this.currentWeather = currentWeather;
    }

    public ArrayList<Weather> getDailyWeather() {
        return dailyWeather;
    }

    public void setDailyWeather(ArrayList<Weather> dailyWeather) {
        this.dailyWeather = dailyWeather;
    }

    // pronostico del tiempo actual
    public void currentWeather(LatLng latLng, Context context) {
        String fullCurrentUrl = basicUrl + currentUrl + "lat=" + latLng.latitude + "&lon=" + latLng.longitude + "&appid=" + context.getString(R.string.open_weather_map_key);

        try {
            URL url = new URL(fullCurrentUrl);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            String line;
            StringBuilder stringBuilder = new StringBuilder("");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            this.currentWeather = new Weather();

            if (jsonObject.has("weather")) {
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject jsonWeather = jsonArray.getJSONObject(0);
                if (jsonWeather.has("main")) {
                    currentWeather.setMainWeather(jsonWeather.getString("main"));
                }
                if (jsonWeather.has("description")) {
                    currentWeather.setWeatherDescription(jsonWeather.getString("description"));
                }
                if (jsonWeather.has("id")) {
                    currentWeather.setId(jsonWeather.getInt("id"));
                    this.currentWeather.setWeatherConsideration(this.weatherConsideration(this.currentWeather.getId()));
                }
                if (jsonWeather.has("icon")) {
                    currentWeather.setIcon(jsonWeather.getString("icon"));
                }
            }

            if (jsonObject.has("main")) {
                JSONObject jsonMain = jsonObject.getJSONObject("main");

                if (jsonMain.has("temp")) {
                    // esta temperatura se obtiene en Kelvin como medida, para llevarla a Celsius se le debe restar 273.15
                    currentWeather.setTemperature(jsonMain.getDouble("temp") - 273.15);
                    this.currentWeather.setTemperatureConsideration(this.temperatureConsideration(this.currentWeather.getTemperature()));
                }
            }

            if (jsonObject.has("dt")) {
                currentWeather.setDay(new Timestamp(jsonObject.getLong("dt")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // llena una lista con el pronostico del tiempo de los siguientes 10 dias
    public void dailyWeather(LatLng latLng, Context context) {

        String fullDailyUrl = WeatherResults.basicUrl + WeatherResults.dailyURL + "lat=" + latLng.latitude + "&lon=" + latLng.longitude + "&appid" + context.getString(R.string.open_weather_map_key);

        try {
            URL url = new URL(fullDailyUrl);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            String line;
            StringBuilder stringBuilder = new StringBuilder("");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JSONObject jsonObjectWhole = new JSONObject(stringBuilder.toString());

            this.dailyWeather = new ArrayList<Weather>();

            if (jsonObjectWhole.has("list")) {
                JSONArray dailyList = jsonObjectWhole.getJSONArray("list");

                for (int i = 0; i < dailyList.length(); i++) {

                    Weather weather = new Weather();

                    JSONObject jsonObject = dailyList.getJSONObject(i);

                    if (jsonObject.has("dt")) {
                        weather.setDay(new Timestamp(jsonObject.getLong("dt")));
                    }

                    if (jsonObject.has("weather")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("weather");
                        JSONObject jsonWeather = jsonArray.getJSONObject(0);

                        if (jsonWeather.has("main")) {
                            weather.setMainWeather(jsonWeather.getString("main"));
                        }
                        if (jsonWeather.has("description")) {
                            weather.setWeatherDescription(jsonWeather.getString("description"));
                        }
                        if (jsonWeather.has("id")) {
                            weather.setId(jsonWeather.getInt("id"));
                            weather.setWeatherConsideration(this.weatherConsideration(weather.getId()));
                        }
                        if (jsonWeather.has("icon")) {
                            weather.setIcon(jsonWeather.getString("icon"));
                        }
                    }

                    if (jsonObject.has("temp")) {

                        JSONObject jsonTemp = jsonObject.getJSONObject("temp");

                        if (jsonTemp.has("day")) {
                            weather.setTemperature(jsonTemp.getLong("day"));
                            weather.setTemperatureConsideration(this.temperatureConsideration(weather.getTemperature()));
                        }
                    }

                    this.dailyWeather.add(weather);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int temperatureConsideration(double temperature) {
        int tempConsideration = -2;
        if (temperature <= 0) {
            tempConsideration = -1;
        }
        if (temperature > 0 && temperature <= 15) {
            tempConsideration = 0;
        }
        if (temperature > 15 && temperature <= 25) {
            tempConsideration = 1;
        }
        if (temperature > 25) {
            tempConsideration = 2;
        }
        return tempConsideration;
    }

    private int weatherConsideration(int idWeather) {
        int weatConsideration = 4; // inicializado por default en catastrofe

        if ((idWeather + "").charAt(0) == 8) {
            weatConsideration = 0;
        }
        if ((idWeather + "").charAt(0) == 3) {
            weatConsideration = 1;
        }
        if ((idWeather + "").charAt(0) == 5) {
            weatConsideration = 2;
        }
        if ((idWeather + "").charAt(0) == 6) {
            weatConsideration = 3;
        }

        return weatConsideration;
    }



    public static int getIconByWeatherHorary(Activity activity, int weather, int horary){
        String mDrawableName = "weather_" +weather+""+horary;
        int resID = activity.getResources().getIdentifier(mDrawableName , "mipmap", activity.getPackageName());
        return resID;
    }

}
