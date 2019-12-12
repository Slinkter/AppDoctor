package com.cudpast.app.doctor.doctorApp.Business;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.PacienteProfile;
import com.cudpast.app.doctor.doctorApp.Model.DoctorProfile;
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

            DoctorProfile currentDoctor = Common.currentUserDoctor;
            PacienteProfile currentPaciente = Common.currentPaciente;

            tv_paciente_firstname.setText(currentPaciente.getFirstname());
            tv_paciente_lastName.setText(currentPaciente.getLastname());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
