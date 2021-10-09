package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BookMarkActivity extends AppCompatActivity {

    Button btn_favorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark);

        btn_favorites = findViewById(R.id.btn_favorites);

        btn_favorites.setOnClickListener(new View.OnClickListener() {





        });
    }

}