package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mainactivity.category_search.CategoryResult;
import com.example.mainactivity.category_search.Document;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    MapPOIItem currentMarker;
    EditText editTextQuery;
    RecyclerView recyclerview;
    private static int REQUEST_ACCESS_FINE_LOCATION = 1000;
    //final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Document> documentArrayList = new ArrayList<>();
        editTextQuery = (EditText) findViewById(R.id.editTextQuery);
        recyclerview = findViewById(R.id.main_recyclerview);
        LocationAdapter locationAdapter = new LocationAdapter(documentArrayList, getApplicationContext(), editTextQuery, recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(locationAdapter);

        MapView mapView = new MapView(this);
        RelativeLayout mapViewContainer = (RelativeLayout) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            } else {
            }
        } else {
        } //위치권한 허용 묻는 팝업

        final LocationListener putMarkerOnCurrentLocation = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {
                currentMarker = new MapPOIItem();

                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                currentMarker.setItemName("현재 위치");
                currentMarker.setTag(0);
                currentMarker.setMapPoint(mapPoint);
                currentMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                mapView.addPOIItem(currentMarker);


            }
        };


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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"위치 권한이 허용되었습니다", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
