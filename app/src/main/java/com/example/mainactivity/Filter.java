package com.example.mainactivity;

import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;

import static com.example.mainactivity.GpsTracker.circleByGPS;
import static com.example.mainactivity.MainActivity.circleByLocal;
import static com.example.mainactivity.MainActivity.lm;
import static com.example.mainactivity.MainActivity.mapView;

public class Filter extends AppCompatActivity {
    EditText edit_radius;
    CheckBox filter_both, filter_seperate, filter_urinal, filter_toilet, filter_handicap, filter_kid;
    public static String gender;
    public static int urinal = 0, toilet = 0, handicap = 0, kid = 0;
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView(R.layout.filter);
        edit_radius = findViewById(R.id.edit_radius);
        edit_radius.setHint("현재설정 : " + GpsTracker.radius + "m");

        filter_both = findViewById(R.id.filter_both);
        filter_seperate = findViewById(R.id.filter_seperate);
        filter_urinal = findViewById(R.id.filter_urinal);
        filter_toilet = findViewById(R.id.filter_toilet);
        filter_handicap = findViewById(R.id.filter_handicap);
        filter_kid = findViewById(R.id.filter_kid);

        filter_both.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(filter_both.isChecked() | filter_seperate.isChecked()){
                    if(!filter_seperate.isChecked()){
                        gender = "Y";
                    }else if (!filter_both.isChecked()){
                        gender = "N";
                    }else gender = "both";
                }
            }
        });
        filter_seperate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(filter_both.isChecked() | filter_seperate.isChecked()){
                    if(!filter_seperate.isChecked()){
                        gender = "Y";
                    }else if (!filter_both.isChecked()){
                        gender = "N";
                    }else gender = "both";
                }
            }
        });
        filter_urinal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(filter_urinal.isChecked()){
                    urinal = 0;
                }else urinal = Integer.MAX_VALUE;
            }
        });
        filter_toilet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(filter_toilet.isChecked()){
                    toilet = 0;
                }else toilet = Integer.MAX_VALUE;
            }
        });
        filter_handicap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(filter_handicap.isChecked()){
                    handicap = 0;
                }else handicap = Integer.MAX_VALUE;
            }
        });
        filter_kid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(filter_kid.isChecked()){
                    kid = 0;
                }else kid = Integer.MAX_VALUE;
            }
        });
    }

    public void btn_radius_onclick(View view){

        try{
            int radius = Integer.parseInt(edit_radius.getText().toString());
            if(radius > 5000){
                Toast.makeText(this,"5km 이하로 설정 가능합니다", Toast.LENGTH_SHORT).show();
            }
            else if(radius < 0){
                Toast.makeText(this, "0이상의 수만 입력 가능합니다", Toast.LENGTH_SHORT).show();
            }
            else{
                if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    MainActivity.mapView.removeAllCircles();
                    GpsTracker.radius = radius;
                    circleByLocal.setRadius(radius);
                    circleByLocal = new MapCircle(
                            MapPoint.mapPointWithGeoCoord(MainActivity.current_latitude, MainActivity.current_longitude), // center
                            GpsTracker.radius, // radius
                            Color.argb(128, 0, 0, 0), // strokeColor
                            Color.argb(40, 0, 0, 255) // fillColor
                    );
                    mapView.addCircle(circleByLocal);
                    GpsTracker.markerUpdate(MainActivity.current_latitude, MainActivity.current_longitude);
                    edit_radius.setText("");
                    edit_radius.setHint("현재설정 : " + GpsTracker.radius + "m");
                }
                else{
                    MainActivity.mapView.removeAllCircles();
                    GpsTracker.radius = radius;
                    circleByGPS.setRadius(radius);
                    circleByGPS = new MapCircle(
                            MapPoint.mapPointWithGeoCoord(MainActivity.current_latitude, MainActivity.current_longitude), // center
                            GpsTracker.radius, // radius
                            Color.argb(128, 0, 0, 0), // strokeColor
                            Color.argb(40, 0, 0, 255) // fillColor
                    );
                    mapView.addCircle(circleByGPS);
                    GpsTracker.markerUpdate(MainActivity.current_latitude, MainActivity.current_longitude);
                    edit_radius.setText("");
                    edit_radius.setHint("현재설정 : " + GpsTracker.radius + "m");
                }
            }
        }catch (Exception e){
            Toast.makeText(this,"잘못된 입력입니다.",Toast.LENGTH_SHORT).show();
        }

    }
}
