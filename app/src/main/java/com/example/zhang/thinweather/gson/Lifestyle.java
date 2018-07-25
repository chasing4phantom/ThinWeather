package com.example.zhang.thinweather.gson;

import com.google.gson.annotations.SerializedName;

public class Lifestyle {
    public String type;

    @SerializedName("brf")
    public String briefinfo;

    @SerializedName("txt")
    public String info;
}
