package com.example.zhang.thinweather.util;

import android.text.TextUtils;
import android.util.Log;


import com.example.zhang.thinweather.gson.AQI;
import com.example.zhang.thinweather.gson.City;
import com.example.zhang.thinweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhang on 2018/7/21.
 */

public class Utility {

/*    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for(int i =0; i< allProvinces.length(); i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }*//*解析处理返回的省级数据*//*

    public static boolean handleCityResponse(String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for(int i =0 ; i < allCities.length(); i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }*//*解析处理返回的市级数据*//*

    public static boolean handleCountyResponse(String response, int CityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for(int i=0; i < allCounties.length(); i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setCityId(countyObject.getInt("id"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(CityId);
                    county.save();
                }
                return true;
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }*/

    /*将json数据解析为Weather实体类*/
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static AQI handleAqiResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String aqiContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(aqiContent,AQI.class);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    public static City handleCityResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String cityContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(cityContent,City.class);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

}
