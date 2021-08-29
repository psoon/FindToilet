package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity {
    TextInputEditText login_id, login_pw;
    CheckBox autoLogin;
    Button btn_login;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login_id = findViewById(R.id.et_login_id);
        login_pw = findViewById(R.id.et_login_pw);
        btn_login = findViewById(R.id.btn_login);
        autoLogin = findViewById(R.id.autoLogin);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sf = getSharedPreferences("autologin", MODE_PRIVATE);
        if(!sf.getString("id", "NULL").equals("NULL")){
            login_id.setText(sf.getString("id", ""));
        }
        if(!sf.getString("pw", "NULL").equals("NULL")){
            login_pw.setText(sf.getString("pw",""));
        }
        if(sf.getBoolean("checked", false)){
            autoLogin.setChecked(true);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String email = login_id.getText().toString().trim();
                    String pw = login_pw.getText().toString().trim();
                    mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if(autoLogin.isChecked()){
                                    SharedPreferences sf = getSharedPreferences("autologin", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sf.edit();
                                    editor.putString("id", email);
                                    editor.putString("pw", pw);
                                    editor.putBoolean("checked", true);
                                    editor.commit();
                                }
                                Toast.makeText(loginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                                MainActivity.setUser(mAuth.getCurrentUser());
                                finish();
                            }else{
                                Toast.makeText(loginActivity.this, "이메일 혹은 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch(Exception e){
                    Toast.makeText(v.getContext(), "공백 혹은 잘못된 입력입니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}