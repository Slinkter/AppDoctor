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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-->Login silencioso
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //<--
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
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            metodoSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    metodoSignInResult(googleSignInResult);
                }
            });
        }
    }


    //METODO SUPORTE
    //1.SALIR
    public void Salir(View view) {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogIngScreen();
                } else {
                    Toast.makeText(getApplicationContext(), "No se puedo salir", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //2.CERRAR
    public void Cerra_sesion(View view) {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogIngScreen();
                } else {
                    Toast.makeText(getApplicationContext(), "no se puedo salir", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //3.OBTENER DATOS DEL USUARIO
    private void metodoSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            if (getIntent() != null){


                try {
                    if (getIntent().getExtras().getString("usuario") != null){
                        String name = getIntent().getExtras().getString("usuario");
                        GoogleSignInAccount account = result.getSignInAccount();
                        nameTextView.setText(name);
                        emailTextView.setText(name);
                        idTextView.setText(account.getId());
                        Glide.with(this).load(account.getPhotoUrl()).into(photoImageView);
                        Log.e("MAINACTIVITY ", " getIntent : id_login_name -->" +name);

                    }else {
                        goLogIngScreen();
                    }
                }catch (Exception e){
                    goLogIngScreen();
                    e.printStackTrace();
                }




            }else {
                goLogIngScreen();
            }


        } else {
            //SI NO ESTA LOGEADO
            goLogIngScreen();
        }
    }
    //4.LOGEAR
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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
