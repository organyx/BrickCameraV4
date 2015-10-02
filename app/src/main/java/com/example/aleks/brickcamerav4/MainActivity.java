package com.example.aleks.brickcamerav4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String BROADCAST_WEATHER = "BROADCAST_WEATHER";
    public static final String TEMPERATURE_WEATHER = "TEMPERATURE_WEATHER";

    TextView tvWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvWeather = (TextView) findViewById(R.id.tvWeather);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBtnVidClick(View view) {
        Intent vidIntent = new Intent(MainActivity.this, VidActivity.class);
        startActivity(vidIntent);
    }

    public void onBtnPicClick(View view) {
        Intent picIntent = new Intent(MainActivity.this, PicActivity.class);
        startActivity(picIntent);
    }

    public void onBtnWeatherClick(View view) {
        Intent updateWeatherIntent = new Intent(MainActivity.this, WeatherService.class);
        MainActivity.this.startService(updateWeatherIntent);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null)
            {
                String temp = bundle.getString(TEMPERATURE_WEATHER);
                {
                    if(temp != null && temp.compareTo("") != 0)
                    {
                        tvWeather.setText("Temperature: " + temp);
                    }
                }
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(BROADCAST_WEATHER));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
