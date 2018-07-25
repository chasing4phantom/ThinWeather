package com.example.zhang.thinweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("fl")
    public String feeltemperature;

    @SerializedName("cond_txt")
    public String info;

    @SerializedName("cond_code")
    public String code;
}
