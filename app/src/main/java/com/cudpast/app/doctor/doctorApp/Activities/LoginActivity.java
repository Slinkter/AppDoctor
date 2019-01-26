package com.cudpast.app.doctor.doctorApp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.User;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Soporte.VolleyRP;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";
    private static String IP = "http://www.cudpast.com/AppDoctor/Login_GETID.php?id=";

    EditText usernamelogin;
    EditText passwordlogin;

    private RequestQueue mRequest;
    private VolleyRP volleyRP;

    private Button btnIngresar;

    private String USER = "";
    private String PASSWORD = "";

    Animation animation;
    private Vibrator vib;


    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        volleyRP = VolleyRP.getInstance(this);
        mRequest = volleyRP.getRequestQueue();

        usernamelogin = findViewById(R.id.loginUsername);
        passwordlogin = findViewById(R.id.loginPassword);
        btnIngresar = findViewById(R.id.btnLogin);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        auth = FirebaseAuth.getInstance();

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitForm()) {
                    //Login con Godaddy
                    VerificarLogin(usernamelogin.getText().toString(), passwordlogin.getText().toString());
                }
            }
        });
    }

    private void VerificarLogin(String sUser, String sPassword) {

        USER = sUser;
        PASSWORD = sPassword;
        String URL = IP + sUser;
        JsonObjectRequest solicitudGoDaddy;

        final SpotsDialog waitingDialog = new SpotsDialog(LoginActivity.this);
        waitingDialog.show();

        solicitudGoDaddy = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {

                try {
                    String estado = datos.getString("resultado");
                    if (estado.equals("CC")) {
                        JSONObject jsondatos = new JSONObject(datos.getString("datos"));
                        String usuario = jsondatos.getString("dniusuario");

                        String emailLogin = usernamelogin.getText().toString();
                        String passwordLogin = passwordlogin.getText().toString();

                        if (usuario.equalsIgnoreCase(emailLogin)){
                            //->
                            VerificacionFirebase(emailLogin,passwordLogin);
                            //<-
                        }

                        waitingDialog.dismiss();

                    } else {
                        Toast.makeText(LoginActivity.this, "Usuario no existe", Toast.LENGTH_SHORT).show();
                        waitingDialog.dismiss();
                    }

                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                waitingDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Esto es un error de ejecución", Toast.LENGTH_LONG).show();
            }
        });

        VolleyRP.addToQueue(solicitudGoDaddy, mRequest, this, volleyRP);
    }

    public void VerificacionFirebase(String usernamelogin , String passwordlogin ){

        String emailLogin = usernamelogin+"@doctor.com";
        String passwordLogin = passwordlogin;


        auth.signInWithEmailAndPassword(emailLogin,passwordLogin )
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseDatabase
                                .getInstance()//Conexion a base de datos --> projectmedical001
                                .getReference(Common.tb_Info_Doctor)//nombre de la tabla-->
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())//recuperar el Uid
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Common.currentUser = dataSnapshot.getValue(Usuario.class);
                                        Log.e("LoginActivity", "Common.currentUser -->" + dataSnapshot.getValue(User.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("ERROR", "DatabaseError -->" + databaseError.toString());
                                    }
                                });


                        Intent intent = new Intent(LoginActivity.this   , VerificacionLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("usuario", Common.currentUser.getFirstname());
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show();


            }
        });

    }




    //Validación de formulario parte 2
    private boolean checkDNI() {
        if (usernamelogin.length() < 8) {
            usernamelogin.setError("Error : ingresar  8 digitos");
            return false;
        }

        if (usernamelogin.getText().toString().trim().isEmpty()) {
            usernamelogin.setError("vacio");
            return false;
        }


        return true;
    }

    private boolean checkPassword() {
        if (passwordlogin.length() < 2) {
            passwordlogin.setError("Error : ingresar password");
            return false;
        }

        if (passwordlogin.getText().toString().trim().isEmpty()) {
            passwordlogin.setError("vacio");
            return false;
        }


        return true;
    }

    private boolean submitForm() {
        if (!checkDNI()) {
            usernamelogin.setAnimation(animation);
            usernamelogin.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkPassword()) {
            usernamelogin.setAnimation(animation);
            usernamelogin.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }


        return true;
    }

    // Funcional 100%
    public void signup(View view) {
        Intent intent = new Intent(this, VerificacionRegistroActivity.class);
        startActivity(intent);
        finish();
    }


}
