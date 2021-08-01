package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class IntroActivity extends AppCompatActivity {
    String filename = "dataset";
    int row = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        AssetManager assetManager = getResources().getAssets();
        try {
            for(int i = 1; i<4; i++){
                CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(assetManager.open(filename + i + ".csv"),"euc-kr")));
                String[] nextLine;
                while((nextLine = reader.readNext()) != null){
                    for(int o = 0; o < 19; o++){
                        MainActivity.dataArr[row][o] = nextLine[o];
                    }
                    row++;
                }

            }


        } catch (Exception e) {
            Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show();
        }

        IntroThread introThread = new IntroThread(handler);
        introThread.start();
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == 1){
                Intent intent =  new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };
}