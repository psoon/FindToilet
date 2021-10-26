package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.mainactivity.MainActivity.dataArr;

public class BookMarkActivity extends AppCompatActivity {

    Button ystar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark);

        ystar = findViewById(R.id.ystar);

        ystar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<BookMarkModel> bookmarkArrayList = new ArrayList<>();
                FirebaseDatabase.getInstance().getReference().child("bookmarks").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

    public void addBookMark(String toiletname, String latitude, String longitude){

    }

    public void delBookMark(String toiletname, String latitude, String longitude){

    }

}