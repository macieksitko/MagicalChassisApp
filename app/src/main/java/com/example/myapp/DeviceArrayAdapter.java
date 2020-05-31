package com.example.myapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeviceArrayAdapter extends ArrayAdapter<Device> {


    private int resourceLayout;
    private Context mContext;

    public DeviceArrayAdapter (Context context, ArrayList<Device> values) {
        super(context,0, values);

        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Device device=getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rowlayout, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        TextView textView2 = (TextView) convertView.findViewById(R.id.text2);
        if (device!=null) {
            textView.setText(device.deviceName);
            if (device.isConnected == 1) {
                textView2.setText("Connected");
                textView.setTextColor(Color.parseColor("#6666ff"));
                textView2.setTextColor(Color.parseColor("#6666ff"));
            } else {
                textView2.setText("Not connected");
                textView.setTextColor(Color.parseColor("#867979"));
                textView2.setTextColor(Color.parseColor("#867979"));
            }
        }
        return convertView;
    }
}

