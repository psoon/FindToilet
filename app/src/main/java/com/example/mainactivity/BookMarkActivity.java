package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.example.mainactivity.MainActivity.dataArr;

public class BookMarkActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    LinearLayout listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark);

        listview = findViewById(R.id.bookmarklist);

        try{
            FileInputStream fis = openFileInput("bookmark.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String str=reader.readLine();
            int i = 0;

            while(str!=null){
                if(i%2==0){
                    TextView tv = new TextView(getApplicationContext());
                    tv.setText(str);
                    tv.setTextSize(25);
                    tv.setGravity(Gravity.CENTER);
                    listview.addView(tv);
                    str=reader.readLine();
                    i++;
                }else{
                    TextView tv = new TextView(getApplicationContext());
                    tv.setText(str);
                    tv.setTextSize(15);
                    tv.setTextColor(Color.GRAY);
                    tv.setGravity(Gravity.CENTER);
                    listview.addView(tv);
                    str=reader.readLine();
                    i++;
                }
            }
            reader.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}