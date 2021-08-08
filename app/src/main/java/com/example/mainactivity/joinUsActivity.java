package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class joinUsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    TextInputEditText etUserId, etUserPw, etUserPwCheck, etUserNickname, etUserPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_us);
        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        etUserId = findViewById(R.id.et_user_id);
        etUserPw = findViewById(R.id.et_user_pw);
        etUserPwCheck = findViewById(R.id.et_user_pw_check);
        etUserNickname = findViewById(R.id.et_user_nickname);
        etUserPhoneNumber = findViewById(R.id.et_user_phonenumber);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    }

    public void btn_join_onclick(View view) {
        String userId = etUserId.getText().toString().trim();
        String userPw = etUserPw.getText().toString().trim();
        String userPwCheck = etUserPwCheck.getText().toString().trim();
        String userPhoneNumber = etUserPhoneNumber.getText().toString().trim();
        String userNickname = etUserNickname.getText().toString().trim();

        if(userPw.equals(userPwCheck)){
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("진행중입니다...");
            dialog.show();
            mAuth.createUserWithEmailAndPassword(userId, userPw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        dialog.dismiss();

                        FirebaseUser user = mAuth.getCurrentUser();
                        String email = user.getEmail();
                        String uid = user.getUid();
                        String name = userNickname;
                        String phonenumber = userPhoneNumber;

                        HashMap hashMap = new HashMap<>();
                        hashMap.put("uid", uid);
                        hashMap.put("email",email);
                        hashMap.put("name",name);
                        hashMap.put("phonenumber", phonenumber);

                        mDatabase.setValue(hashMap);
                        Intent intent = new Intent(joinUsActivity.this, loginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(joinUsActivity.this,"회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        dialog.dismiss();
                        Toast.makeText(joinUsActivity.this, "중복된 이메일 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }else{
            Toast.makeText(joinUsActivity.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}