package com.cudpast.app.doctor.doctorApp.Business;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.UserPaciente;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;

public class DoctorEnd extends AppCompatActivity {


    private TextView tv_paciente_firstname;
    private TextView tv_paciente_lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin);
        getSupportActionBar().hide();

        tv_paciente_firstname = findViewById(R.id.fin_pacienteFirstName);
        tv_paciente_lastName = findViewById(R.id.fin_pacienteLastName);

        metodoSignInResult();

    }

    private void metodoSignInResult() {
        try {

            Usuario currentDoctor = Common.currentUserDoctor;
            UserPaciente currentPaciente = Common.currentPaciente;

            tv_paciente_firstname.setText(currentPaciente.getNombre());
            tv_paciente_lastName.setText(currentPaciente.getApellido());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
