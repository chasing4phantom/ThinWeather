package com.example.zhang.thinweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zhang.thinweather.R;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mData;
    private List<String> cid;

    public ListAdapter(Context context,List<String> mData,List<String> cid){
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = mData;
        this.cid = cid;
    }

    @Override
    public int getCount(){
        return mData.size();
    }

    @Override
    public Object getItem(int position){
        return mData.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertview == null) {
            convertview = mInflater.inflate(R.layout.city_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = convertview.findViewById(R.id.city_item);
            convertview.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertview.getTag();
        }
           viewHolder.mTextView.setText(mData.get(position));
            return convertview;

    }
    public void clear(){
        mData.clear();
        cid.clear();
        notifyDataSetChanged();

    }

    private final class ViewHolder{
        TextView mTextView;
    }
}
