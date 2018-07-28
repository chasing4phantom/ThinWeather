package com.example.zhang.thinweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.zhang.thinweather.gson.AQI;
import com.example.zhang.thinweather.gson.Weather;
import com.example.zhang.thinweather.util.HttpUtil;
import com.example.zhang.thinweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateInfoService extends Service {
    public UpdateInfoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent ,int flags, int startId){
        updateWeather();
        updateAqi();
        updateBingpic();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int cycle = 4 * 60 * 60 * 1000;//4小时毫秒数
        long triggerAttime = SystemClock.elapsedRealtime()+cycle;
        Intent i = new Intent(this,UpdateInfoService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAttime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    private void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        String cid = preferences.getString("cid","null");
        if(weatherString !=null){

            String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" + cid + "&key=d865f88a27d34451ad929db6405a5aeb";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if(weather != null && weather.status.equals("ok")){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateInfoService.this).edit();
                        editor.putString("weather",null);
                        editor.apply();
                    }
                }
            });
        }
    }

    private void updateAqi(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String aqiString = preferences.getString("aqi",null);
        String cid = preferences.getString("cid","null");
        if(aqiString !=null){
            String aqiUrl = "https://free-api.heweather.com/s6/air?location="+cid+"&key=d865f88a27d34451ad929db6405a5aeb";
            HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    AQI aqi = Utility.handleAqiResponse(responseText);
                    if(aqi != null && aqi.status.equals("ok")){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateInfoService.this).edit();
                        editor.putString("aqi",null);
                        editor.apply();
                    }
                }
            });
        }
    }

    private void updateBingpic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingpic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateInfoService.this).edit();
                editor.putString("bing_pic",bingpic);
                editor.apply();
            }
        });
    }
}
