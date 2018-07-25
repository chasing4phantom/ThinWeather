package com.example.zhang.thinweather.gson;

import com.google.gson.annotations.SerializedName;

public class Aqi_now {
    public String aqi;

    public String pm25;

    @SerializedName("qlty")
    public String quality;
}
