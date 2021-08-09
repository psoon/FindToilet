package com.example.mainactivity;

import androidx.annotation.NonNull;

public class UserModel {
    public String uid;
    public String nickName;
    public String phoneNumber;

    public UserModel() {
    }

    public UserModel(String uid, String nickName, String phoneNumber) {
        this.uid = uid;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}