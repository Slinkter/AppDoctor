package com.cudpast.app.doctor.doctorApp.Activities.Extra;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.R;

public class TestActivity extends AppCompatActivity {

    TextView id_title, id_body, id_pToken, id_dToken, id_json_lat_log, id_pacienteUID;


    public String title;
    public String body;
    public String pToken;
    public String dToken;
    public String json_lat_log;
    public String pacienteUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        id_title = findViewById(R.id.id_title);
        id_body = findViewById(R.id.id_body);
        id_pToken = findViewById(R.id.id_pToken);
        id_dToken = findViewById(R.id.id_dToken);
        id_json_lat_log = findViewById(R.id.id_json_lat_log);
        id_pacienteUID = findViewById(R.id.id_pacienteUID);

        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
            body = getIntent().getStringExtra("body");
            pToken = getIntent().getStringExtra("pToken");
            dToken = getIntent().getStringExtra("dToken");
            json_lat_log = getIntent().getStringExtra("json_lat_log");
            pacienteUID = getIntent().getStringExtra("pacienteUID");

            id_title.setText(title);
            id_body.setText(body);
            id_pToken.setText(pToken);
            id_dToken.setText(dToken);
            id_json_lat_log.setText(json_lat_log);
            id_pacienteUID.setText(pacienteUID);

        }


    }
}
