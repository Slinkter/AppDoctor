package com.cudpast.app.doctor.doctorApp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity {


    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        photoImageView = findViewById(R.id.idFotoUsuario);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        idTextView = findViewById(R.id.idTextView);


    }
    // METODO PRINCIPAL

    @Override
    protected void onStart() {
        super.onStart();

        Usuario usuario = Common.currentUser;
        if (usuario != null) {
            Toast.makeText(this, "Hola", Toast.LENGTH_SHORT).show();
            metodoSignInResult();
        } else {
            goLogIngScreen();
        }

    }


    //METODO SUPORTE
    //1.SALIR
    public void Salir(View view) {


    }


    //3.OBTENER DATOS DEL USUARIO
    private void metodoSignInResult() {

        try {

            Usuario usuario = Common.currentUser;
            Log.e("usuario ------> ", usuario.getFirstname() + " \n" + usuario.getImage());

            nameTextView.setText(usuario.getFirstname());
            emailTextView.setText(usuario.getCorreoG());
            idTextView.setText(usuario.getDni());
            Glide.with(this).load(usuario.getImage()).into(photoImageView);


        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    //4.LOGIN_ACTIVITY
    private void goLogIngScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //5.IR A LA ACTIVIDDAD PRINCIPAL
    public void Atender(View view) {
        Intent intent = new Intent(this, DoctorHome.class);
        startActivity(intent);
        finish();
    }


}
