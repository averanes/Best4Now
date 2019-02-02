package com.adoble.best4now.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.adoble.best4now.R;
import com.adoble.best4now.domain.Weather;
import com.adoble.best4now.ui.MainActivity;
import com.adoble.best4now.ui.MapsFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class WeatherRequest extends AsyncTask<LatLng, Integer, Weather> {
    // private static final String APP_ID = "ce2ed02687e124b11c4a3a8eb72eb60b"; // OPEN WEATHER MAP KEY

    // https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=ce2ed02687e124b11c4a3a8eb72eb60b
    // https://api.openweathermap.org/data/2.5/forecast/daily?lat=35&lon=139&cnt=10&appid=ce2ed02687e124b11c4a3a8eb72eb60b
    private static String basicUrl = "https://api.openweathermap.org/data/2.5/";
    private static String currentUrl = "weather?";
    private static String dailyURL = "forecast/daily?";

    private ArrayList<Weather> dailyWeather; //NO se usa



    public WeatherRequest() {
    }


    @Override
    protected Weather doInBackground(LatLng... latLng) {
        String fullCurrentUrl = basicUrl + currentUrl + "lat=" + latLng[0].latitude + "&lon=" + latLng[0].longitude + "&appid=" + MainActivity.mainActivity.getString(R.string.open_weather_map_key);

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

            httpURLConnection.disconnect();

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            Weather currentWeather = new Weather();

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
                    currentWeather.setWeatherConsideration(this.weatherConsideration(currentWeather.getId()));
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
                    currentWeather.setTemperatureConsideration(this.temperatureConsideration(currentWeather.getTemperature()));
                }
            }

            if (jsonObject.has("dt")) {
                currentWeather.setDay(jsonObject.getLong("dt") * 1000L);
            }



            //************obtain the time ***********
            GregorianCalendar g=new GregorianCalendar();
            long timestamp = g.getTimeInMillis()/1000; // Current UTC date/time expressed as seconds since midnight, January 1, 1970 UTC

            String fullCurrentUrlTime = "https://maps.googleapis.com/maps/api/timezone/json?location="+latLng[0].latitude + "," + latLng[0].longitude+"&timestamp="+timestamp+"&key="+ MainActivity.mainActivity.getString(R.string.google_maps_key);

                URL url2 = new URL(fullCurrentUrlTime);

                HttpURLConnection httpURLConnection2 = (HttpURLConnection) url2.openConnection();
                httpURLConnection2.setRequestMethod("GET");
                httpURLConnection2.connect();

                String line2;
                StringBuilder stringBuilder2 = new StringBuilder("");
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpURLConnection2.getInputStream()));
                while ((line2 = bufferedReader2.readLine()) != null) {
                    stringBuilder2.append(line2);
                }


             jsonObject = new JSONObject(stringBuilder2.toString()); // convert returned JSON string to JSON object

                String status =jsonObject.getString("status");

                long rawOffset =jsonObject.getLong("rawOffset");

                long offsets =jsonObject.getLong("dstOffset") * 1000 + rawOffset * 1000; // get DST and time zone offsets in milliseconds


                 currentWeather.setDay(System.currentTimeMillis() + offsets - (new GregorianCalendar().getTimeZone().getRawOffset()));

                httpURLConnection2.disconnect();

            return currentWeather;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Weather weatherP) {
        super.onPostExecute(weatherP);

        if(weatherP!=null)
            MainActivity.mainActivity.setWeather(weatherP);
    }

    public ArrayList<Weather> getDailyWeather() {
        return dailyWeather;
    }

    public void setDailyWeather(ArrayList<Weather> dailyWeather) {
        this.dailyWeather = dailyWeather;
    }



    // llena una lista con el pronostico del tiempo de los siguientes 10 dias
    public void dailyWeather(LatLng latLng, Context context) {

        String fullDailyUrl = WeatherRequest.basicUrl + WeatherRequest.dailyURL + "lat=" + latLng.latitude + "&lon=" + latLng.longitude + "&appid" + context.getString(R.string.open_weather_map_key);

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
                        weather.setDay(jsonObject.getLong("dt") * 1000);
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
        // baja temperatura
        int tempConsideration = 0;

        // temperatura normal
        if (temperature >= 7 && temperature < 31) {
            tempConsideration = 1;
        }

        // temperatura alta
        if (temperature >= 31) {
            tempConsideration = 2;
        }

        return tempConsideration;
    }

    private int weatherConsideration(int idWeather) {
        int weatConsideration = 2; // inicializado por default en catastrofe o nieve

        switch ((idWeather + "").charAt(0)){
            // buen tiempo
            case '8': weatConsideration = 0;
            break;
            // lluvia ligera (tiempo moderado)
            case '3':
            case '5': weatConsideration = 1;
            break;
            // lluvia ligera (tiempo moderado)
            case '7':
               if(idWeather <= 761 && idWeather != 711)
                weatConsideration = 1;
               break;
            case '6':
                   if(idWeather == 600 || idWeather == 611 || idWeather == 615)
                       weatConsideration = 1;
                   break;

        }
        return weatConsideration;
    }



    public static int getIconByWeatherHorary(Activity activity, int weather, int horary){
        if(weather > 3) weather = 3;
        if(horary > 2) horary = 2;
        if(horary == 1) horary = 0;

        String mDrawableName = "weather_" +weather+""+horary;
        int resID = activity.getResources().getIdentifier(mDrawableName , "mipmap", activity.getPackageName());
        return resID;
    }

}