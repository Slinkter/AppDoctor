package com.cudpast.app.doctor.doctorregisterapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONException;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cudpast.app.doctor.doctorregisterapp.R;
import com.cudpast.app.doctor.doctorregisterapp.Model.Usuario;
import com.cudpast.app.doctor.doctorregisterapp.Soporte.VolleyRP;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.android.volley.toolbox.JsonObjectRequest;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleApiClient;

    private static final String IP_REGISTRAR = "http://www.cudpast.com/AppDoctor/Registro_INSERT.php";
    private RequestQueue mRequest;
    private VolleyRP volleyRP;

    EditText signupDNI, signupName, signupLast, signupNumPhone, signupCodMePe, signupUser, signupPassword;
    Button guardar, salir;
    Animation animation;
    private Vibrator vib;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        guardar = findViewById(R.id.btnGuardar);
        salir = findViewById(R.id.btnSalir);
        volleyRP = VolleyRP.getInstance(this);
        mRequest = volleyRP.getRequestQueue();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        signupName = findViewById(R.id.signupName);
        signupLast = findViewById(R.id.signupLast);
        signupNumPhone = findViewById(R.id.signupNumPhone);
        signupUser = findViewById(R.id.signupUser); //<--- dirección
        signupCodMePe = findViewById(R.id.signupCodMePe);
        signupDNI = findViewById(R.id.signupDNI);
        signupPassword = findViewById(R.id.signupPassword);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstname = signupName.getText().toString();
                String lastname = signupLast.getText().toString();
                String numphone = signupNumPhone.getText().toString();
                String usuario = signupUser.getText().toString(); // <--- dirección
                String codmedpe = signupCodMePe.getText().toString();
                String dni = signupDNI.getText().toString();
                String password = signupPassword.getText().toString();
                String correoG = getIntent().getExtras().getString("correog");
                String fecha = getCurrentTimeStamp();
                if (submitForm()) {
                    registrarWebGoDaddy(dni, firstname, lastname, numphone, codmedpe, usuario, password, correoG, fecha);
                    Usuario user1 = new Usuario(dni, firstname, lastname, numphone, codmedpe, usuario, password, correoG, fecha);
                    databaseReference.child("usuarios").child(dni).setValue(user1);
                    iniciarActivity();
                }
            }
        });


        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date());
            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void iniciarActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    //Insertar en la base de datos de Godaddy
    public void registrarWebGoDaddy(String dni, String firstname, String lastname, String numphone, String codmedpe, String usuario, String password, String correoG, String fecha) {
        HashMap<String, String> hashMapRegistro = new HashMap<>();
        hashMapRegistro.put("idDNI",dni);
        hashMapRegistro.put("nombre",firstname);
        hashMapRegistro.put("apellido",lastname);
        hashMapRegistro.put("telefono",numphone);
        hashMapRegistro.put("codMedico",codmedpe);
        hashMapRegistro.put("direccion",usuario);
        hashMapRegistro.put("password",password);
        hashMapRegistro.put("correo",correoG);
        hashMapRegistro.put("fecha",fecha);

        JsonObjectRequest solicitar = new JsonObjectRequest(Request.Method.POST,
                IP_REGISTRAR,
                new JSONObject(hashMapRegistro),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject datos) {
                        try {
                            String estado = datos.getString("resultado");
                            if (estado.equalsIgnoreCase("Datos registrados  :) ")) {
                                Toast.makeText(RegisterActivity.this, estado, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, estado, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "no se pudo registrar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "no se pudo registrar", Toast.LENGTH_SHORT).show();
            }
        });
        VolleyRP.addToQueue(solicitar, mRequest, this, volleyRP);
    }

    //Validación de formulario parte 1
    private boolean submitForm() {

        if (!checkDNI()) {
            signupDNI.setAnimation(animation);
            signupDNI.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkName()) {
            signupName.setAnimation(animation);
            signupName.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkLast()) {
            signupLast.setAnimation(animation);
            signupLast.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkNumPhone()) {
            signupNumPhone.setAnimation(animation);
            signupNumPhone.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkCodMe()) {
            signupCodMePe.setAnimation(animation);
            signupCodMePe.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkUser()) {
            signupUser.setAnimation(animation);
            signupUser.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkPassword()) {
            signupPassword.setAnimation(animation);
            signupPassword.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }
        return true;
    }

    //Validación de formulario parte 2
    private boolean checkDNI() {
        if (signupDNI.length() < 8) {
            signupDNI.setError("Error : ingresar  8 digitos");
            return false;
        }
        return true;
    }

    private boolean checkName() {
        if (signupName.getText().toString().trim().isEmpty()) {
            signupName.setError("Error ingresar nombre");
            return false;
        }
        return true;
    }

    private boolean checkLast() {
        if (signupLast.getText().toString().trim().isEmpty()) {
            signupLast.setError("Error ingresar apellido");
            return false;
        }
        return true;
    }

    private boolean checkNumPhone() {
        if (signupNumPhone.length() < 5) {
            signupNumPhone.setError("Error ingresar telefono");
            return false;
        }
        return true;
    }

    private boolean checkCodMe() {
        if (signupCodMePe.length() < 6) {
            signupCodMePe.setError("Error ingresar codigo de medico");
            return false;
        }
        return true;
    }

    private boolean checkUser() {
        if (signupUser.getText().toString().trim().isEmpty()) {
            signupUser.setError("Error ingresar usuario");
            return false;
        }
        return true;
    }

    private boolean checkPassword() {
        if (signupPassword.getText().toString().trim().isEmpty()) {
            signupPassword.setError("Error ingresar password");
            return false;
        }
        return true;
    }

}
