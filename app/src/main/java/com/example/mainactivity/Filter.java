package com.example.mainactivity;

import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
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
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView(R.layout.filter);
        edit_radius = findViewById(R.id.edit_radius);
        edit_radius.setHint("현재설정 : " + GpsTracker.radius + "m");
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
