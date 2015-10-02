package com.example.aleks.brickcamerav4;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherHelper {

    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    public static String getTemperature(String apikey, String where){
        try {
            Log.d("API", apikey);
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, where));
            HttpURLConnection connection =  (HttpURLConnection)url.openConnection();
            connection.addRequestProperty("x-api-key", apikey);
            BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null) {
                json.append(tmp).append("\n");
            }
            reader.close();
            JSONObject data = new JSONObject(json.toString());
            if(data.getInt("cod") == 200){
                JSONObject details = data.getJSONArray("weather").getJSONObject(0);
                JSONObject main = data.getJSONObject("main");
                String temp = String.format("%.2f", (main.getDouble("temp") + Math.random()))+ " ℃";
                return temp;
            }
        }catch(Exception e){
            return "";
        }
        return "";
    }
}





