package com.cudpast.app.doctor.doctorApp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;
    private Button btn_salir_MainActivity;
    private GoogleApiClient mGoogleApiCliente;

    //todo al cerra session todavia se muestra al doctor en el mapa del paciente


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mGoogleApiCliente = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiCliente.connect();

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


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
