package com.example.zhang.thinweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class City {
    public String status;

    @SerializedName("basic")
    public List<CityBasic> cityBasicList;
}
