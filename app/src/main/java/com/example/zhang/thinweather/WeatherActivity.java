package com.example.zhang.thinweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhang.thinweather.gson.AQI;
import com.example.zhang.thinweather.gson.Forecast;
import com.example.zhang.thinweather.gson.Lifestyle;
import com.example.zhang.thinweather.gson.Weather;
import com.example.zhang.thinweather.service.UpdateInfoService;
import com.example.zhang.thinweather.util.HttpUtil;
import com.example.zhang.thinweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private ImageView changeCity;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private LinearLayout lifestyleLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private ImageView weatherInfoImage;

    private ImageView bingPicImg;

    private String cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //绑定控件
        bingPicImg = findViewById(R.id.bing_pic_img);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        changeCity = findViewById(R.id.change_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        weatherInfoImage = findViewById(R.id.weather_info_image);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        lifestyleLayout = findViewById(R.id.lifestyle_layout);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        String aqiString = preferences.getString("aqi",null);
        if(weatherString != null && aqiString !=null){
            //有缓存
            Weather weather = Utility.handleWeatherResponse(weatherString);
            AQI aqi = Utility.handleAqiResponse(aqiString);
            showWeatherInfo(weather);
            showAqiInfo(aqi);
        }else{
            //无缓存
            //String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.VISIBLE);
            requestAqi("auto_ip");
            requestWeather("auto_ip");
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cid = preferences.getString("cid","auto_ip");
                requestAqi(cid);
                requestWeather(cid);

            }
        });

        String bingPic = preferences.getString("bing_pic",null);
        if(bingPic !=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        changeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this,ChangeCityActivity.class);
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    cid = data.getStringExtra("cid");
                    swipeRefresh.setRefreshing(true);
                    requestWeather(cid);
                    requestAqi(cid);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("cid",data.getStringExtra("cid"));
                    editor.apply();
                }
                break;
                default:
        }
    }
    //请求当前城市的常规天气数据集合
    public void requestWeather(String cid){
        String weatherUrl;
        if(("auto_ip").equals(cid)) {
            weatherUrl = "https://free-api.heweather.com/s6/weather?location=auto_ip&key=d865f88a27d34451ad929db6405a5aeb";
        }else{
            weatherUrl = "https://free-api.heweather.com/s6/weather?location=" + cid + "&key=d865f88a27d34451ad929db6405a5aeb";
        }
        Log.d(weatherUrl.substring(1,68), "requestWeather: ");
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                 final String responseText = response.body().string();
                 final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                            loadBingPic();
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取信息失败",Toast.LENGTH_SHORT).show();

                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

    }

    //请求当前城市的空气质量实况
    public void requestAqi(String cid){
        String weatherUrl;
        if(("auto_ip").equals(cid)) {
            weatherUrl = "https://free-api.heweather.com/s6/air?location=auto_ip&key=d865f88a27d34451ad929db6405a5aeb";

        }else{
            weatherUrl = "https://free-api.heweather.com/s6/air?location="+cid+"&key=d865f88a27d34451ad929db6405a5aeb";
        }
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取空气质量信息失败",Toast.LENGTH_SHORT).show();
                        //swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final AQI aqi = Utility.handleAqiResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(aqi != null && "ok".equals(aqi.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("aqi",responseText);
                            editor.apply();
                            showAqiInfo(aqi);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取aqi信息失败",Toast.LENGTH_SHORT).show();

                        }
                        //swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    //展示常规天气数据集合
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.info;
        Integer code = Integer.parseInt(weather.now.code);
        titleCity.setText(cityName);
        titleUpdateTime.setText("最后更新 "+updateTime);
        degreeText.setText(degree);
        weatherInfoImage.setImageLevel(code);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxTominText = view.findViewById(R.id.max_to_min_text);
            dateText.setText(forecast.date);
            if(forecast.cond_txt_d == forecast.cond_txt_n){
                infoText.setText(forecast.cond_txt_d);
            }else{
                infoText.setText(forecast.cond_txt_d +"转"+forecast.cond_txt_n);
            }
            maxTominText.setText(forecast.tmp_max +"/"+forecast.tmp_min +"℃");
            forecastLayout.addView(view);
        }
        lifestyleLayout.removeAllViews();
        for(Lifestyle lifestyle : weather.lifestyleList){
            View view = LayoutInflater.from(this).inflate(R.layout.lifestyle_item,lifestyleLayout,false);
            TextView typeText = view.findViewById(R.id.lifestyle_type);
            TextView brfText = view.findViewById(R.id.lifestyle_brf);
            TextView txtText = view.findViewById(R.id.lifestyle_text);
            switch(lifestyle.type){
                case "comf":
                    typeText.setText("舒适度指数");
                    break;
                case "cw":
                    typeText.setText("洗车指数");
                    break;
                case "drsg":
                    typeText.setText("穿衣指数");
                    break;
                case "flu":
                    typeText.setText("感冒指数");
                    break;
                case "sport":
                    typeText.setText("运动指数");
                    break;
                case "trav":
                    typeText.setText("旅游指数");
                    break;
                case "uv":
                    typeText.setText("紫外线指数");
                    break;
                case "air":
                    typeText.setText("空气污染扩散条件指数");
                    break;
                case "ac":
                    typeText.setText("空调开启指数");
                    break;
                case "ag":
                    typeText.setText("过敏指数");
                    break;
                case "gl":
                    typeText.setText("太阳镜指数");
                    break;
                case "mu":
                    typeText.setText("化妆指数");
                    break;
                case "airc":
                    typeText.setText("晾晒指数");
                    break;
                case "ptfc":
                    typeText.setText("交通指数");
                    break;
                case "fisin":
                    typeText.setText("钓鱼指数");
                    break;
                case "spi":
                    typeText.setText("防晒指数");
                    break;
                    default:
                        break;

            }
            brfText.setText(lifestyle.briefinfo);
            txtText.setText(lifestyle.info);
            lifestyleLayout.addView(view);
        }
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, UpdateInfoService.class);
        startService(intent);
    }

    //展示空气质量实况
    private void showAqiInfo(AQI aqi){

            aqiText.setText(aqi.air_now_city.aqi);
            pm25Text.setText(aqi.air_now_city.pm25);

    }

    //加载必应每日美图
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
               final String bingPic = response.body().string();
               SharedPreferences .Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                        }
               });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
