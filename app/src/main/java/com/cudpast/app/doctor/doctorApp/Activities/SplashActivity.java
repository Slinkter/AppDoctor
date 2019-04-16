package com.cudpast.app.doctor.doctorApp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Fragment.Fragment_2;
import com.cudpast.app.doctor.doctorApp.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 3000;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);


        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, DoctorHome.class);
                startActivity(intent);
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, SPLASH_SCREEN_DELAY);


    }





}
