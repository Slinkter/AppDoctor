package com.cudpast.app.doctor.doctorApp.Business;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cudpast.app.doctor.doctorApp.Activities.MainActivity;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.R;

public class DoctorCancelOnRoad extends AppCompatActivity {

    private static final String TAG = DoctorCancelOnRoad.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_doctor_cancel_on_road);
    }

    public void returnMainTimeOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
