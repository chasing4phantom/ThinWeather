package com.example.zhang.thinweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zhang.thinweather.adapter.ListAdapter;
import com.example.zhang.thinweather.gson.City;
import com.example.zhang.thinweather.gson.CityBasic;
import com.example.zhang.thinweather.util.HttpUtil;
import com.example.zhang.thinweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangeCityActivity extends AppCompatActivity {
    private EditText searchcity;

    private ListView resultcitylist;
    private List<String> mData = new ArrayList<>();
    private List<String> cid = new ArrayList<>();
    private ListAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_changecity);
        //绑定控件
        searchcity = findViewById(R.id.edit_text);
        resultcitylist = findViewById(R.id.listview_city);
        adapter = new ListAdapter(ChangeCityActivity.this,mData,cid);
        resultcitylist.setAdapter(adapter);
        searchcity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.clear();
                requestCity(s.toString());
            }
        });
        resultcitylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cID = cid.get(position);
                Intent intent = new Intent();
                intent.putExtra("cid",cID);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    public void requestCity(String city){
        String cityurl = "https://search.heweather.com/find?location="+city+"&key=d865f88a27d34451ad929db6405a5aeb";
        HttpUtil.sendOkHttpRequest(cityurl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangeCityActivity.this,"获取城市失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final City city = Utility.handleCityResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(city !=null && city.status.equals("ok")){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ChangeCityActivity.this).edit();
                            editor.putString("city",responseText);
                            editor.apply();

                            showCityInfo(city);
                        }else{
                            Toast.makeText(ChangeCityActivity.this,"获取信息失败",Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
    }

    private void showCityInfo(City city){
        for(CityBasic cityBasic: city.cityBasicList){
            mData.add(cityBasic.location+"--"+cityBasic.parent_city+"--"+cityBasic.admin_area);
            cid.add(cityBasic.cid);
        }
        adapter.notifyDataSetChanged();
    }


}
