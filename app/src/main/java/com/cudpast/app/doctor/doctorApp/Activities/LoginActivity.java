package com.cudpast.app.doctor.doctorApp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Soporte.VolleyRP;

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


        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (submitForm()) {

                    VerificarLogin(usernamelogin.getText().toString(), passwordlogin.getText().toString());
                }


            }
        });


    }

    private void VerificarLogin(String sUser, String sPassword) {
        USER = sUser;
        PASSWORD = sPassword;


        SolicutudJSON(IP + sUser);

    }

    public void SolicutudJSON(String URL) {

        final SpotsDialog waitingDialog = new SpotsDialog(LoginActivity.this, R.style.VericacionLogin);
        waitingDialog.show();

        JsonObjectRequest solicitud = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                waitingDialog.dismiss();
                verificarLoginURL(datos);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Esto es un error de ejecución", Toast.LENGTH_LONG).show();

            }
        });

        VolleyRP.addToQueue(solicitud, mRequest, this, volleyRP);
    }

    private void verificarLoginURL(JSONObject datos) {

        try {
            String estado = datos.getString("resultado");
            if (estado.equals("CC")) {
                JSONObject jsondatos = new JSONObject(datos.getString("datos"));
                String usuario = jsondatos.getString("dniusuario");
                String password = jsondatos.getString("password");
                if (usuario.equals(USER) && password.equals(PASSWORD)) {
                    Toast.makeText(LoginActivity.this, " ====== * ====== \n" + "Bienvenido : " + usuario + "\n", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, VerificacionLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "====== * ====== \n" + "usuario o contraseña no es válido", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, estado, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {

        }

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
