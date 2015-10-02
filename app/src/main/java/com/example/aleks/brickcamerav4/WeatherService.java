package com.example.aleks.brickcamerav4;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class WeatherService extends Service {
    public WeatherService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        new GetWeatherAndPublish().execute();

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class GetWeatherAndPublish extends AsyncTask<Integer, Void, String> {
        String key;

        @Override
        protected void onPreExecute() {
            key = getApplicationContext().getString(R.string.open_weather_apikey);
        }

        @Override
        protected String doInBackground(Integer... params) {
            return WeatherHelper.getTemperature(key, "copenhagen, dk");
        }

        @Override
        protected void onPostExecute(String result)
        {
            publishWeather(result);
        }
    }

    private void publishWeather(String temperature)
    {
        Intent intent = new Intent(MainActivity.BROADCAST_WEATHER);
        intent.putExtra(MainActivity.TEMPERATURE_WEATHER, temperature);
        sendBroadcast(intent);
    }
}
