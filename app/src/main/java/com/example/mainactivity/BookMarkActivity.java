package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class BookMarkActivity extends AppCompatActivity {

    ListView listview;
    BookMarkAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark);

        listview = findViewById(R.id.bookmarklist);
        adapter = new BookMarkAdapter();
        listview.setAdapter(adapter);

        try{
            FileInputStream fis = openFileInput("bookmark.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line=reader.readLine();

            while(line!=null){
                adapter.addItem(line);
                adapter.notifyDataSetChanged();
                line = reader.readLine();
            }
            reader.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}