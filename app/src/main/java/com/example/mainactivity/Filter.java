package com.example.mainactivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    CheckBox filter_both, filter_seperate,  filter_handicap, filter_kid;
    Button filter_btn_login, filter_btn_join;
//    CheckBox filter_urinal, filter_toilet,
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView(R.layout.filter);
        edit_radius = findViewById(R.id.edit_radius);
        edit_radius.setHint("현재설정 : " + GpsTracker.radius + "m");
        filter_both = findViewById(R.id.filter_both);
        filter_seperate = findViewById(R.id.filter_seperate);
//        filter_urinal = findViewById(R.id.filter_urinal);
//        filter_toilet = findViewById(R.id.filter_toilet);
        filter_handicap = findViewById(R.id.filter_handicap);
        filter_kid = findViewById(R.id.filter_kid);

        filter_btn_login=findViewById(R.id.filter_btn_login);
        filter_btn_join=findViewById(R.id.filter_btn_join);

        if(GpsTracker.gender.equals("both")){
            filter_both.setChecked(true);
            filter_seperate.setChecked(true);
        }else if(GpsTracker.gender.equals("N")){
            filter_seperate.setChecked(true);
        }else filter_both.setChecked(true);

//        if(GpsTracker.urinal == Integer.MAX_VALUE){
//            filter_urinal.setChecked(false);
//        }else filter_urinal.setChecked(true);
//        if(GpsTracker.toilet == Integer.MAX_VALUE){
//            filter_toilet.setChecked(false);
//        }else filter_toilet.setChecked(true);
        if(GpsTracker.handicap == Integer.MAX_VALUE){
            filter_handicap.setChecked(false);
        }else filter_handicap.setChecked(true);
        if(GpsTracker.kid == Integer.MAX_VALUE){
            filter_kid.setChecked(false);
        }else filter_kid.setChecked(true);


        filter_both.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(filter_both.isChecked() | filter_seperate.isChecked()){
                    if(!filter_seperate.isChecked()){
                        GpsTracker.gender = "Y";
                    }else if (!filter_both.isChecked()){
                        GpsTracker.gender = "N";
                    }else GpsTracker.gender = "both";
                }
            }
        });
        filter_seperate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(filter_both.isChecked() | filter_seperate.isChecked()){
                    if(!filter_seperate.isChecked()){
                        GpsTracker.gender = "Y";
                    }else if (!filter_both.isChecked()){
                        GpsTracker.gender = "N";
                    }else GpsTracker.gender = "both";
                }
            }
        });
//        filter_urinal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(filter_urinal.isChecked()){
//                    GpsTracker.urinal = 0;
//                }else GpsTracker.urinal = Integer.MAX_VALUE;
//            }
//        });
//        filter_toilet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(filter_toilet.isChecked()){
//                    GpsTracker.toilet = 0;
//                }else GpsTracker.toilet = Integer.MAX_VALUE;
//            }
//        });
        filter_handicap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(filter_handicap.isChecked()){
                    GpsTracker.handicap = 0;
                }else GpsTracker.handicap = Integer.MAX_VALUE;
            }
        });
        filter_kid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(filter_kid.isChecked()){
                    GpsTracker.kid = 0;
                }else GpsTracker.kid = Integer.MAX_VALUE;
            }
        });

        filter_btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Filter.this, loginActivity.class);
                startActivity(intent);
            }
        });
        filter_btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Filter.this, joinUsActivity.class);
                startActivity(intent);
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
    public void onDestroy() {
        MainActivity.gpsTracker.markerUpdate(MainActivity.current_latitude, MainActivity.current_longitude);
        super.onDestroy();
    }
}
