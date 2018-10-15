package com.cudpast.app.doctor.doctorregisterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        volleyRP = VolleyRP.getInstance(this);
        mRequest = volleyRP.getRequestQueue();

        usernamelogin = findViewById(R.id.loginUsername);
        passwordlogin = findViewById(R.id.loginPassword);
        btnIngresar = findViewById(R.id.btnLogin);


        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerificarLogin(usernamelogin.getText().toString(), passwordlogin.getText().toString());
            }
        });


    }

    private void VerificarLogin(String sUser, String sPassword) {
        USER = sUser;
        PASSWORD = sPassword;

        Toast.makeText(this, "Verificando ", Toast.LENGTH_SHORT).show();

        SolicutudJSON(IP + sUser);

    }

    public void SolicutudJSON(String URL) {
        JsonObjectRequest solicitud = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
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
                    Toast.makeText(LoginActivity.this, "Servidor Godday \n ====== * ====== \n" + "Bienvenido : " + usuario + "\n", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Servidor Godday \n ====== * ====== \n" + "usuario o contraseña no es válido", Toast.LENGTH_LONG).show();
                }


            } else {
                Toast.makeText(LoginActivity.this, estado, Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {

        }

    }


    private void goToActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    // 100 % mas boton
    public void signup(View view) {
        Intent intent = new Intent(this, VerificacionActivity.class);
        startActivity(intent);
        finish();
    }
}
