package com.example.mainactivity;

public class BookMarkModel {
    public String uid;;
    public String toiletNum;
    //public Object createAt;
    public BookMarkModel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToiletNum() {
        return toiletNum;
    }

    public void setToiletNum(String toiletNum) {
        this.toiletNum = toiletNum;
    }

    public BookMarkModel(String uid, String toiletNum){
        this.uid = uid;
        this.toiletNum = toiletNum;
    }
}
