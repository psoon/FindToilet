package com.example.mainactivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.MapViewEventListener{
    public static EditText editTextQuery;
    RecyclerView recyclerview;
    ImageButton btn_filter, btn_stt;
    FloatingActionButton fab_refresh;
    FloatingActionButton ystar;
    Button btn_search, btn_favorites,btn_navigation,btn_siren, btn_comment_summit;
    SlidingUpPanelLayout panel;
    TextView location_name, location_addr, tv_gender, tv_serviceTime;
    EditText comment;
    Intent intent;
    SpeechRecognizer speechRecognizer;
    CommentAdapter commentAdapter;
    RecyclerView comment_recyclerview;
    final int PERMISSION = 1;

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
    private static FirebaseUser user;


    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = mDatabase.getReference();

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
        ystar=findViewById(R.id.ystar);
        btn_search = findViewById(R.id.btnSearch);
        btn_favorites =findViewById(R.id.btn_favorites);
        btn_navigation=findViewById(R.id.btn_navigation);
        btn_siren=findViewById(R.id.btn_siren);
        panel = findViewById(R.id.slidingPanel);
        location_name = findViewById(R.id.location_name);
        location_addr = findViewById(R.id.location_addr);
        tv_gender = findViewById(R.id.tv_gender);
        tv_serviceTime = findViewById(R.id.tv_serviceTime);
        btn_comment_summit=findViewById(R.id.btn_comment_summit);
        comment = findViewById(R.id.comment);
        btn_stt = findViewById(R.id.btn_stt);

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
        mapView.setMapViewEventListener(this);
        panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        btn_stt.setOnClickListener(v -> {
            if ( Build.VERSION.SDK_INT >= 23 ){
                int SttPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
                if (SttPermissionCheck == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},PERMISSION);
                } else {
                    speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
                    speechRecognizer.setRecognitionListener(listener);
                    speechRecognizer.startListening(intent);
                }
            }
        });
        panel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) { }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.HIDDEN){
                    fab_refresh.setVisibility(View.VISIBLE);
                    ystar.setVisibility(View.VISIBLE);
                    if(comment_recyclerview!=null){
                        comment_recyclerview.setVisibility(View.GONE);
                    }
                }else if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){
                    fab_refresh.setVisibility(View.INVISIBLE);
                    ystar.setVisibility(View.INVISIBLE);
                    if(comment_recyclerview!=null){
                        comment_recyclerview.setVisibility(View.VISIBLE);
                    }
                }else if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    fab_refresh.setVisibility(View.VISIBLE);
                    ystar.setVisibility(View.VISIBLE);
                    if(comment_recyclerview!=null){
                        comment_recyclerview.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        ystar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BookMarkActivity.class);
                startActivity(intent);
            }
        });

        editTextQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });
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
        /*btn_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_favorites.setSelected(!btn_favorites.isSelected());
            }
        });*/

    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"원하는 장소를 말해주세요",Toast.LENGTH_SHORT).show(); }
        @Override public void onBeginningOfSpeech() {}
        @Override public void onRmsChanged(float rmsdB) {}
        @Override public void onBufferReceived(byte[] buffer) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onError(int error) {
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "음성인식 허용이 되지 않았습니다";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트워크 상태를 확인해주세요";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "다시 한번 정확히 말해주세요";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "이미 실행 중입니다";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "시간이 초과되었습니다";
                    break;
                default: message = "알 수 없는 오류가 발생하였습니다";
                    break;
            }
            Toast.makeText(getApplicationContext(), "error : " + message,Toast.LENGTH_SHORT).show();
        }
        @Override public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i = 0; i < matches.size() ; i++){
                editTextQuery.setText(matches.get(i)); } }

        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "위치 권한이 허용되었습니다", Toast.LENGTH_SHORT).show();
                moveOnCurrentLocation();
            }
            else if(permissions[0].equals(Manifest.permission.INTERNET) || permissions[0].equals(Manifest.permission.RECORD_AUDIO)){
                Toast.makeText(this, "마이크 권한이 허용되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if(permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this,"현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
            else if(permissions[0].equals(Manifest.permission.INTERNET) || permissions[0].equals(Manifest.permission.RECORD_AUDIO)){
                Toast.makeText(this, "마이크를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
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
            FileInputStream fis = openFileInput("bookmark.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            boolean isIn = false;
            while((line=reader.readLine()) != null) {
                if(line.equals(Integer.toString(tag))){
                    isIn = true;
                }
            }
            if(isIn) btn_favorites.setSelected(true);
            else btn_favorites.setSelected(false);
        }catch(Exception e){ }
        try{
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            location_name.setText(dataArr[tag][1]);
            if(dataArr[tag][2]!=null && !dataArr[tag][2].equals("")){
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

        //댓글 불러오기 구현
        ArrayList<CommentModel> commentArrayList = new ArrayList<>();
        comment_recyclerview = findViewById(R.id.recyclerView_comment);

        commentAdapter = new CommentAdapter(commentArrayList, MainActivity.this, comment_recyclerview);
        comment_recyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.HORIZONTAL));
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        comment_recyclerview.setLayoutManager(layoutManager2);
        comment_recyclerview.setAdapter(commentAdapter);

        FirebaseDatabase.getInstance().getReference().child("Toilet_Comment").child(dataArr[tag][1]).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    commentAdapter.addItem(dataSnapshot.getValue(CommentModel.class));
                }
                commentAdapter.notifyDataSetChanged();
            }
        });
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


        //즐겨찾기 버튼 클릭
        btn_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_favorites.isSelected()){
                    String favoriteList = "";
                    try{
                        FileInputStream fis = openFileInput("bookmark.txt");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

                        String line;

                        while((line=reader.readLine())!=null){
                            if(line.equals(Integer.toString(tag))){
                                continue;
                            }
                            favoriteList += (line+"\n");
                        }
                        FileWriter fw = new FileWriter(getFilesDir()+"/bookmark.txt");
                        fw.write(favoriteList);

                        btn_favorites.setSelected(false);
                        Toast.makeText(getApplicationContext(), "즐겨찾기에서 삭제되었습니다", Toast.LENGTH_SHORT).show();

                        fw.close();
                        reader.close();
                        fis.close();
                    }
                    catch(Exception e){Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();}
                }
                else{
                    try{
                        FileOutputStream fos = openFileOutput("bookmark.txt", Context.MODE_APPEND);
                        PrintWriter writer = new PrintWriter(fos);

                        String favoriteList = Integer.toString(tag) + "\n";
                        writer.write(favoriteList);
                        Toast.makeText(MainActivity.this, "즐겨찾기가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        btn_favorites.setSelected(true);

                        writer.close();
                        fos.close();
                    } catch(Exception e){ }
                }
            }
        });

        //신고 버튼 클릭
        btn_siren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("신고하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final LinearLayout linearLayout = (LinearLayout)View.inflate(MainActivity.this, R.layout.comment_report, null);
                        new AlertDialog.Builder(MainActivity.this)
                                .setView(linearLayout)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        EditText et_report = findViewById(R.id.et_report);
                                        CommentModel model = new CommentModel();
                                        try{
                                            model.content = et_report.getText().toString();
                                        }catch(Exception e){
                                            Toast.makeText(MainActivity.this, "공백 혹은 잘못된 입력입니다.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        model.createAt = ServerValue.TIMESTAMP;
                                        if(user!= null){
                                            try{
                                                model.uid = user.getUid();
                                                model.userName =  databaseReference.child("Users").child(user.getUid()).child("nickName").get().getResult().getValue(String.class);
                                            }catch(Exception e ){
                                                Toast.makeText(MainActivity.this, "실패하였습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        model.toiletNum = Integer.toString(tag);
                                        databaseReference.child("Report").child(dataArr[tag][1]).child(model.content).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(MainActivity.this, "신고되었습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
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
        //댓글 눌렀을 때 로그인 대화상자 뜨기 - 로그인 안 했을 경우 뜨게 하는걸로 변경해야함
        btn_comment_summit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(user==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("로그인이 필요한 서비스 입니다.");
                    final String[] array = new String[] {"로그인","회원가입"};
                    builder.setItems(array, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                Intent intent = new Intent(MainActivity.this, loginActivity.class);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(MainActivity.this, joinUsActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                    builder.setPositiveButton("닫기",null);
                    builder.show();
                } else{
                    //댓글 작성 구현
                    CommentModel commentModel = new CommentModel();
                    commentModel.content = comment.getText().toString();
                    commentModel.uid = user.getUid();
                    commentModel.createAt = ServerValue.TIMESTAMP;
                    commentModel.toiletNum = Integer.toString(tag);
                    if(commentModel.content.length()<=0) {
                        Toast.makeText(MainActivity.this, "공백 혹은 잘못된 입력입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    databaseReference.child("Users").child(user.getUid()).child("nickName").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            String value = task.getResult().getValue(String.class);
                            commentModel.userName = value;
                            commentAdapter.addItem(commentModel);
                            databaseReference.child("Toilet_Comment").child(dataArr[tag][1]).child(comment.getText().toString()).setValue(commentModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MainActivity.this, "댓글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                                    comment.setText("");

                                    commentArrayList.clear();
                                    FirebaseDatabase.getInstance().getReference().child("Toilet_Comment").child(dataArr[tag][1]).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                                                commentAdapter.addItem(dataSnapshot.getValue(CommentModel.class));
                                            }
                                            commentAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

    }
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) { }
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) { }
    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) { }
    @Override
    public void onMapViewInitialized(MapView mapView) { }
    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
    }
    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) { }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) { }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) { }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) { }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) { }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) { }



    @Override
    public void onBackPressed() {
        if(panel.getPanelState()==SlidingUpPanelLayout.PanelState.EXPANDED||panel.getPanelState()==SlidingUpPanelLayout.PanelState.COLLAPSED) {
            panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            return;
        }
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
    public static void setUser(FirebaseUser user) {
        MainActivity.user = user;
    }

    public static FirebaseUser getUser() {
        return user;
    }

    @Override
    protected void onDestroy() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        super.onDestroy();
    }
}
