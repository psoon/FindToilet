package com.example.mainactivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.category_search.CategoryResult;
import com.example.mainactivity.category_search.Document;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.MapViewEventListener{
    public static EditText editTextQuery;
    ProgressDialog loading;
    RecyclerView recyclerview;
    ImageButton btn_filter;
    FloatingActionButton fab_refresh;
    Button btn_search,btn_star,btn_navigation,btn_siren;
    SlidingUpPanelLayout panel;
    TextView location_name, location_addr, tv_gender, tv_serviceTime;
    String [] tvStr = {"대변기수", "소변기수", "장애인 대변기수", "장애인소변기수", "유아용 대변기수", "유아용소변기수", "대변기수", "장애인 대변기수", "유아용대변기수"};
    Integer[] tvId = {R.id.tv_male_toilet, R.id.tv_male_urinal, R.id.tv_male_handiToilet, R.id.tv_male_handiUrinal, R.id.tv_male_kidToilet, R.id.tv_male_kidUrinal,
                        R.id.tv_female_toilet, R.id.tv_female_handiToilet, R.id.tv_female_kidToilet};
    public static MapView mapView;
    public static LocationManager lm;
    private static int REQUEST_ACCESS_FINE_LOCATION = 1000;
    public static GpsTracker gpsTracker;
    public static String[][] dataArr = new String[35754][19];
    public static double current_latitude = 37.5665, current_longitude = 126.9780;
    public static MapCircle circleByLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        ArrayList<Document> documentArrayList = new ArrayList<>();
        editTextQuery = findViewById(R.id.editTextQuery);
        recyclerview = findViewById(R.id.main_recyclerview);
        btn_filter = findViewById(R.id.btn_filter);
        fab_refresh= findViewById(R.id.fab_refresh);
        btn_search = findViewById(R.id.btnSearch);
        btn_star=findViewById(R.id.btn_star);
        btn_navigation=findViewById(R.id.btn_navigation);
        btn_siren=findViewById(R.id.btn_siren);
        panel = findViewById(R.id.slidingPanel);
        location_name = findViewById(R.id.location_name);
        location_addr = findViewById(R.id.location_addr);
        tv_gender = findViewById(R.id.tv_gender);
        tv_serviceTime = findViewById(R.id.tv_serviceTime);
        LocationAdapter locationAdapter = new LocationAdapter(documentArrayList, getApplicationContext(), editTextQuery, recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(locationAdapter);
        mapView = new MapView(this);
        RelativeLayout mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //화면구성 및 변수 초기화

        askPermission();
        mapView.setPOIItemEventListener(this);
        panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);


        editTextQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                recyclerview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >=1 ){
                    documentArrayList.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                    ApiInterface apiInterface = RetrofitApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation("KakaoAK d58052159cf446f527c49bd30884f70c", s.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryResult> call, Throwable t) {

                        }
                    });
                } else{
                    if (s.length() <= 0) {
                        recyclerview.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String s = editTextQuery.getText().toString();
                    documentArrayList.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                    ApiInterface apiInterface = RetrofitApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation("KakaoAK d58052159cf446f527c49bd30884f70c", s, 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryResult> call, Throwable t) {

                        }
                    });
                }catch(Exception e){
                    Toast.makeText(MainActivity.this,"검색에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapViewContainer.removeAllViews();
                mapView = new MapView(MainActivity.this);
                mapViewContainer.addView(mapView);
                moveOnCurrentLocation();
                mapView.setPOIItemEventListener(MainActivity.this);
            }
        });
        //즐겨찾기 버튼 누르면 색 변경-위치마다 다르게 수정해야함
        btn_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_star.setSelected(!btn_star.isSelected());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"위치 권한이 허용되었습니다", Toast.LENGTH_SHORT).show();
            moveOnCurrentLocation();
        }
        else{
            Toast.makeText(this,"현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void gpsServiceSetting(){
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("gps 비활성화");
        builder.setMessage("현재 위치를 가져오기 위해 gps를 활성화 하시겠습니까?");
        builder.setPositiveButton("활성화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gpsSetting = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsSetting);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "위치정보를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude), true);
                    circleByLocal = new MapCircle(
                            MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude), // center
                            GpsTracker.radius, // radius
                            Color.argb(128, 0, 0, 0), // strokeColor
                            Color.argb(40, 0, 0, 255) // fillColor
                    );
                    mapView.addCircle(circleByLocal);
                    circleByLocal.setCenter(MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude));
                    GpsTracker.markerUpdate(current_latitude, current_longitude);

                    mapView.setMapViewEventListener(MainActivity.this);
                }
            }
        });
        builder.create().show();
    }
    public void moveOnCurrentLocation(){
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            gpsServiceSetting();
        }
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            gpsTracker = new GpsTracker(MainActivity.this);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude), true);
                }
            }, 3000);
        }
    }
    public static double distance(double latitude1, double longitude1, double latitude2, double longitude2) {

        double theta = longitude1 - longitude2;
        double dist = Math.sin(deg2rad(latitude1)) * Math.sin(deg2rad(latitude2)) + Math.cos(deg2rad(latitude1)) * Math.cos(deg2rad(latitude2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;
        dist = Math.abs(dist);
        return dist;
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public void btn_filter_onClick(View view) {
        Intent intent = new Intent(this, Filter.class);
        startActivity(intent);
    }

//    public void login(View view) {
//        Intent intent = new Intent(this, loginActivity.class);
//        startActivity(intent);
//    }
//
//    public void join(View view) {
//        Intent intent = new Intent(this, joinUsActivity.class);
//        startActivity(intent);
//    }
    public void askPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            } else {
                moveOnCurrentLocation();
            }
        } else { } //위치권한 허용 묻는 코드
    }
    public void MarkerListener(){
        mapView.setPOIItemEventListener(MainActivity.this);
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        int tag = mapPOIItem.getTag();
        try{
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            location_name.setText(dataArr[tag][1]);
            if(dataArr[tag][2]!=null){
                location_addr.setText(dataArr[tag][2]);
            } else location_addr.setText(dataArr[tag][3]);
            if(dataArr[tag][4]!=null){
                if(dataArr[tag][4].equals("N")){
                    tv_gender.setText("남여공용 여부: N");
                }else tv_gender.setText("남여공용 여부: Y");
            }
            for(int i = 0; i<tvId.length; i++){
                int idx = i + 5;
                TextView tmpTv = findViewById(tvId[i]);
                tmpTv.setText(dataArr[tag][idx]);
                //tmpTv.setText(tvStr[i] + dataArr[tag][idx]);
            }
            if(dataArr[tag][16]!=null){
                tv_serviceTime.setText("운영시간: "+ dataArr[tag][16]);
            }

        }catch(Exception e){
            Toast.makeText(MainActivity.this, "정보를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
        }
        //길찾기 버튼 클릭
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://map.kakao.com/link/map/" + dataArr[tag][1] + ","
                        + dataArr[tag][17] + "," + dataArr[tag][18];
                Intent openURL = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(openURL);
            }
        });
        btn_siren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("신고하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //신고할 경우 구현
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.setTitle("신고");
                builder.show();
            }
        });

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        mapView = MainActivity.mapView;
        mapView.removeAllPOIItems();
        current_latitude = mapPoint.getMapPointGeoCoord().latitude;
        current_longitude = mapPoint.getMapPointGeoCoord().longitude;
        mapView.removeCircle(circleByLocal);
        circleByLocal = new MapCircle(
                MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude),
                GpsTracker.radius,
                Color.argb(128, 0, 0, 0), // strokeColor
                Color.argb(40, 0, 0, 255)
        );
        mapView.addCircle(circleByLocal);
        MapPOIItem markerOnCenter = new MapPOIItem();
        markerOnCenter.setItemName("기준점");
        markerOnCenter.setMapPoint(MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude));
        markerOnCenter.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mapView.addPOIItem(markerOnCenter);
        GpsTracker.markerUpdate(current_latitude, current_longitude);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("종료하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setTitle("화장실 어디야");
        builder.show();
    }
}
