package com.cudpast.app.doctor.doctorApp.Activities.Extra;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.R;

public class TestActivity extends AppCompatActivity {

    TextView id_title, id_body, id_Paciente, id_ubicacion;
    String title, body, Paciente, ubicacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        id_title = findViewById(R.id.id_title);
        id_body = findViewById(R.id.id_body);
        id_Paciente = findViewById(R.id.id_Paciente);
        id_ubicacion = findViewById(R.id.id_ubicacion);


        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
            body = getIntent().getStringExtra("body");
            Paciente = getIntent().getStringExtra("idPaciente");
            ubicacion = getIntent().getStringExtra("ubicacion");

            id_title.setText(title);
            id_body.setText(body);
            id_Paciente.setText(Paciente);
            id_ubicacion.setText(ubicacion);

        }


    }
}
