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
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.User;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Soporte.VolleyRP;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static String IP = "http://www.cudpast.com/AppDoctor/Login_GETID.php?id=";

    private EditText emailLogin;
    private EditText passwordlogin;

    private RequestQueue mRequest;
    private VolleyRP volleyRP;

    private Button btnIngresar;

    private Animation animation;
    private Vibrator vib;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        //
        volleyRP = VolleyRP.getInstance(this);
        mRequest = volleyRP.getRequestQueue();
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //XML
        emailLogin = findViewById(R.id.loginUsernameEmail);
        passwordlogin = findViewById(R.id.loginPassword);
        btnIngresar = findViewById(R.id.btnLogin);
        //FIREBASE INIT
        auth = FirebaseAuth.getInstance();

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitForm()) {
                    //Login con Godaddy
                    // VerificarLogin(emailLogin.getText().toString());
                    String email ="consultingarsiapp@gmail.com";
                    String pwd = "MUNDOverde20";
//                    VerificacionFirebase(emailLogin.getText().toString(), passwordlogin.getText().toString());
                    VerificacionFirebase(email,pwd);
                }
            }
        });
    }


    //2.AUTENTICACION CON FIREBASE
    public void VerificacionFirebase(String usernamelogin, String passwordlogin) {

        String emailLogin = usernamelogin;
        String passwordLogin = passwordlogin;

        Log.e(TAG, "143:emailLogin" + emailLogin);
        Log.e(TAG, "144:passwordLogin" + passwordLogin);

        final SpotsDialog waitingDialog = new SpotsDialog(LoginActivity.this, R.style.DialogLogin);
        waitingDialog.show();

        auth
                .signInWithEmailAndPassword(emailLogin, passwordLogin)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.e(TAG, "148:signInWithEmail:success");
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userAuthId = firebaseUser.getUid();

                        Log.e(TAG, "user " + firebaseUser);
                        Log.e(TAG, "userAuthId " + userAuthId);

                        if (firebaseUser.isEmailVerified()) {
                            updateUI(firebaseUser);
                            FirebaseDatabase
                                    .getInstance()//Conexion a base de datos --> projectmedical001
                                    .getReference(Common.TB_INFO_DOCTOR)//tabla-->
                                    .child(userAuthId)//recuperar el Uid
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            try {
                                                waitingDialog.dismiss();
                                                Usuario userAndroid = dataSnapshot.getValue(Usuario.class);
                                                Common.currentUser = userAndroid;
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            Log.e("LoginActivity", "190 : tu usuario " + dataSnapshot.getValue(User.class));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            waitingDialog.dismiss();
                                            Log.e("ERROR", "DatabaseError -->" + databaseError.toString());
                                            updateUI(null);
                                        }
                                    });


                        } else {
                            updateUI(null);
                            waitingDialog.dismiss();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "signInWithEmail:failure  " + e.getMessage());
                Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show();
                updateUI(null);
                waitingDialog.dismiss();

            }
        });

    }


    //Validación de formulario parte 2
    private boolean checkDNI() {

        if (emailLogin.length() < 8) {
            emailLogin.setError("Error : ingresar  8 digitos");
            return false;
        }
        if (emailLogin.getText().toString().trim().isEmpty()) {
            emailLogin.setError("vacio");
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
            emailLogin.setAnimation(animation);
            emailLogin.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }
        if (!checkPassword()) {
            emailLogin.setAnimation(animation);
            emailLogin.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }
        return true;
    }

    // Funcional 100%
    public void signup(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser usuarioFirebase) {
        if (usuarioFirebase != null) {
            if (usuarioFirebase.isEmailVerified()) {
                // Toast.makeText(this, "Correo verificado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "correo no verificado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    //1.VERIFICACION DE GODADDY
//    private void VerificarLogin(String sUser) {
//
//        final SpotsDialog waitingDialog = new SpotsDialog(LoginActivity.this, R.style.DialogLogin);
//        waitingDialog.show();
//        String URL = IP + sUser;
//        JsonObjectRequest solicitudGoDaddy;
//
//        solicitudGoDaddy = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject datos) {
//
//                try {
//                    String estado = datos.getString("resultado");
//                    if (estado.equals("CC")) {
//
//                        JSONObject jsondatos = new JSONObject(datos.getString("datos"));
//                        String usuario = jsondatos.getString("dniusuario");
//                        String emailLogin = LoginActivity.this.emailLogin.getText().toString();
//                        String passwordLogin = passwordlogin.getText().toString();
//
//                        if (usuario.equalsIgnoreCase(emailLogin)) {
//                          //  VerificacionFirebase(emailLogin, passwordLogin);
//                        }
//                        waitingDialog.dismiss();
//                    } else {
//                        Toast.makeText(LoginActivity.this, "Usuario no existe", Toast.LENGTH_SHORT).show();
//                        waitingDialog.dismiss();
//                    }
//
//                } catch (JSONException e) {
//
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                waitingDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Esto es un error de ejecución", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        VolleyRP.addToQueue(solicitudGoDaddy, mRequest, this, volleyRP);
//    }
}
