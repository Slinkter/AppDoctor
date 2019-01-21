package com.cudpast.app.doctor.doctorApp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.R;

public class MainActivity extends AppCompatActivity {

    TextView intentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentUser = findViewById(R.id.intentUser);
        String usuario = getIntent().getExtras().getString("usuario");

        intentUser.setText(usuario);
    }
}
