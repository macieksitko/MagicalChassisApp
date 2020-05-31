package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class CalcActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        TinyDB tinydb = new TinyDB(this);
        ArrayList<Measurement> measurementObjects = tinydb.getListMeasurements("listobjects");

            init(measurementObjects);
    }

    public void init(ArrayList<Measurement> list) {

        TableLayout stk = (TableLayout) findViewById(R.id.tableLayout);

            for(Measurement a: list) {

                int idName = a.id;
                String name = a.measurement;
                if (idName<=13) {
                    TableRow row = new TableRow(this);
                    row.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    row.setMinimumHeight(70);
                    //textView1
                    TextView t1v = new TextView(this);
                    t1v.setText("" + idName);
                    t1v.setTextColor(Color.BLACK);
                    t1v.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    t1v.setTextSize(20);
                    row.addView(t1v);

                    //textView2
                    TextView t2v = new TextView(this);
                    t2v.setText(name);
                    t2v.setTextColor(Color.BLACK);
                    t2v.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    t2v.setTextSize(20);
                    row.addView(t2v);

                    stk.addView(row);
                }
            }

    }
}
