package com.cudpast.app.doctor.doctorApp.Business;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.User;
import com.cudpast.app.doctor.doctorApp.Model.UserPaciente;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.squareup.picasso.Picasso;

public class DoctorFin extends AppCompatActivity {

    private ImageView image_doctor;
    private TextView tv_doctor_firstname;
    private TextView tv_doctor_lastName, c_especialidad, c_tiempo, c_servicio;

    private TextView tv_paciente_firstname;
    private TextView tv_paciente_lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin);
        getSupportActionBar().hide();

        image_doctor = findViewById(R.id.fin_doctorImage);
        tv_doctor_firstname = findViewById(R.id.fin_doctorFirstNameFin);
        tv_doctor_lastName = findViewById(R.id.fin_doctorLastNameFin);

        c_especialidad = findViewById(R.id.c_especialidad);
        c_tiempo = findViewById(R.id.c_tiempo);
        c_servicio = findViewById(R.id.c_servicio);

        tv_paciente_firstname = findViewById(R.id.fin_pacienteFirstName);
        tv_paciente_lastName = findViewById(R.id.fin_pacienteLastName);

        metodoSignInResult();

    }

    private void metodoSignInResult() {
        try {

            Usuario currentDoctor = Common.currentUser;
            UserPaciente currentPaciente = Common.currentPaciente;

            tv_doctor_firstname.setText(currentDoctor.getFirstname());
            tv_doctor_lastName.setText(currentDoctor.getLastname());
            c_especialidad.setText(currentDoctor.getEspecialidad());

            c_tiempo.setText("30 min");
            c_servicio.setText("Consulta medica");


            tv_paciente_firstname.setText(currentPaciente.getNombre());
            tv_paciente_lastName.setText(currentPaciente.getApellido());

            Picasso
                    .with(this)
                    .load(currentDoctor.getImage())
                    .placeholder(R.drawable.ic_doctorapp)
                    .error(R.drawable.ic_doctorapp)
                    .into(image_doctor);

//            Picasso
//                    .with(this)
//                    .load(currentPaciente.getApellido())
//                    .placeholder(R.drawable.ic_boy_svg)
//                    .error(R.drawable.ic_boy_svg)
//                    .into(image_doctor);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
