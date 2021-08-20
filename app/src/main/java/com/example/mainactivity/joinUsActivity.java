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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class joinUsActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    TextInputEditText etUserId, etUserPw, etUserPwCheck, etUserNickname, etUserPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_us);
        etUserId = findViewById(R.id.et_user_id);
        etUserPw = findViewById(R.id.et_user_pw);
        etUserPwCheck = findViewById(R.id.et_user_pw_check);
        etUserNickname = findViewById(R.id.et_user_nickname);
        etUserPhoneNumber = findViewById(R.id.et_user_phonenumber);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();
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
            mAuth.createUserWithEmailAndPassword(userId, userPw).addOnCompleteListener(joinUsActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        dialog.dismiss();

                        FirebaseUser user = mAuth.getCurrentUser();
                        String email = user.getEmail();
                        String uid = task.getResult().getUser().getUid();

                        UserModel userModel = new UserModel();
                        userModel.nickName = userNickname;
                        userModel.phoneNumber = userPhoneNumber;
                        userModel.uid = uid;

                        mDatabase.getReference().child("Users").child(uid).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(joinUsActivity.this,"회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(joinUsActivity.this, loginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else{
                        dialog.dismiss();
                        if(!userId.contains("@")){
                            Toast.makeText(joinUsActivity.this, "올바른 이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                        }
                        else if(userPw.length() < 6){
                            Toast.makeText(joinUsActivity.this, "비밀번호는 6자리 이상이어야 합니다", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(joinUsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                }
            });
        }else{
            Toast.makeText(joinUsActivity.this, "비밀번호가 일치하지 않습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}