package com.cudpast.app.doctor.doctorApp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;
    private Button btn_salir_MainActivity;

    //todo al cerra session todavia se muestra al doctor en el mapa del paciente


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

//        photoImageView = findViewById(R.id.idFotoUsuario);
//        nameTextView = findViewById(R.id.nameTextView);
//        emailTextView = findViewById(R.id.emailTextView);
//        idTextView = findViewById(R.id.idTextView);
//        btn_salir_MainActivity = findViewById(R.id.btn_salir_MainActivity);
//
//        btn_salir_MainActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                goToLoginActivity();
//            }
//        });


    }

    //.METODO PRINCIPAL


    //METODO SUPORTE
    //.OBTENER DATOS DEL USUARIO
    private void metodoSignInResult() {
        try {
            Usuario usuario = Common.currentUser;
            nameTextView.setText(usuario.getFirstname());
            emailTextView.setText(usuario.getCorreoG());
            idTextView.setText(usuario.getDni());
            Picasso
                    .with(this)
                    .load(usuario.getImage())
                    .placeholder(R.drawable.ic_photo_doctor)
                    .error(R.drawable.ic_photo_doctor)
                    .into(photoImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //.IR A LA ACTIVIDDAD PRINCIPAL
    public void Atender(View view) {
        Intent intent = new Intent(this, DoctorHome.class);
        startActivity(intent);
    }

    //.LOGIN_ACTIVITY
    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    //codmedpe,correoG,direccion,dni,especialidad,fecha,firstname, image,lastname,numphone,password;
    //User Android
    @Override
    protected void onStart() {
        super.onStart();
        Usuario usuario = Common.currentUser;
        if (usuario != null) {
            metodoSignInResult();
        } else {
            goToLoginActivity();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
