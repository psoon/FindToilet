package com.example.mainactivity;

public class BookMarkModel {
    public String toiletName;
    public String latitude;
    public String longitude;

    //public Object createAt;
    public BookMarkModel() {
    }

    public String getToiletNum() {
        return toiletName;
    }

    public void setToiletNum(String toiletName) {
        this.toiletName = toiletName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public BookMarkModel(String toiletName, String latitude, String longitude){
        this.toiletName = toiletName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
