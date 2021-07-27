package com.example.mainactivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.daum.mf.map.api.MapView;

public class IntroThread extends Thread{
    private Handler handler;
    public IntroThread(Handler handler){
        this.handler = handler;
    }
    @Override
    public void run() {
        Message msg = new Message();
        try{
            Thread.sleep(3000);
            msg.what=1;
            handler.sendEmptyMessage(msg.what);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
