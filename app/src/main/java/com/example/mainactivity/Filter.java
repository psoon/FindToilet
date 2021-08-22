package com.example.mainactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;

import static com.example.mainactivity.GpsTracker.circleByGPS;
import static com.example.mainactivity.MainActivity.circleByLocal;
import static com.example.mainactivity.MainActivity.lm;
import static com.example.mainactivity.MainActivity.mapView;

public class Filter extends AppCompatActivity {
    EditText edit_radius;
    CheckBox filter_both, filter_seperate,  filter_handicap, filter_kid;
    Button filter_btn_login, filter_btn_join, filter_logout;
    TextView nickname;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView(R.layout.filter);
        edit_radius = findViewById(R.id.edit_radius);
        edit_radius.setHint("현재설정 : " + GpsTracker.radius + "m");
        filter_both = findViewById(R.id.filter_both);
        filter_seperate = findViewById(R.id.filter_seperate);
        filter_handicap = findViewById(R.id.filter_handicap);
        filter_kid = findViewById(R.id.filter_kid);

        FrameLayout content = findViewById(R.id.filter_framelayout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(mAuth.getCurrentUser()!=null){
            inflater.inflate(R.layout.inflater_user_logon, content, true);
            filter_logout = findViewById(R.id.btn_signout);
            nickname = findViewById(R.id.inflater_user_nickname);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Users").child(mAuth.getUid()).child("nickName").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    nickname.setText(task.getResult().getValue(String.class) + " 님");
                }
            });
            filter_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    startActivity(new Intent(Filter.this, Filter.class));
                    finish();
                }
            });
        }else{
            inflater.inflate(R.layout.inflater_user_null, content, true);
            filter_btn_login=findViewById(R.id.filter_btn_login);
            filter_btn_join=findViewById(R.id.filter_btn_join);
            filter_btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Filter.this, loginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            filter_btn_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Filter.this, joinUsActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }



        if(GpsTracker.gender.equals("both")){
            filter_both.setChecked(true);
            filter_seperate.setChecked(true);
        }else if(GpsTracker.gender.equals("N")){
            filter_seperate.setChecked(true);
        }else filter_both.setChecked(true);

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
