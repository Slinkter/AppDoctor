package com.cudpast.app.doctor.doctorApp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Soporte.VolleyRP;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity implements TextWatcher, CompoundButton.OnCheckedChangeListener {
    //
    private static final String TAG = "LoginActivity";

    private RequestQueue mRequest;
    private VolleyRP volleyRP;
    private Button btnIngresar;
    private Animation animation;
    private Vibrator vib;
    private FirebaseAuth auth;
    //
    private EditText ed_login_email, ed_login_pwd;
    private CheckBox checkBox;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String PREF_NAME = "prefs";
    public static final String KEY_REMEMBER = "remeber";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASS = "password";
    //
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    SpotsDialog waitingDialog;

    private TextView txt_forgot_pwd;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        permisos();

        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);

        //
        volleyRP = VolleyRP.getInstance(this);
        mRequest = volleyRP.getRequestQueue();

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        btnIngresar = findViewById(R.id.btnLogin);
        //FIREBASE INIT
        auth = FirebaseAuth.getInstance();
        //
        waitingDialog = new SpotsDialog(LoginActivity.this, R.style.DialogLogin);
        //Check
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        ed_login_email = findViewById(R.id.ed_login_email);
        ed_login_pwd = findViewById(R.id.ed_login_pwd);
        checkBox = (CheckBox) findViewById(R.id.rem_userpass);

        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        ed_login_email.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        ed_login_pwd.setText(sharedPreferences.getString(KEY_PASS, ""));

        ed_login_email.addTextChangedListener(this);
        ed_login_pwd.addTextChangedListener(this);
        checkBox.setOnCheckedChangeListener(this);


        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitForm()) {
                    String email = ed_login_email.getText().toString();
                    String pwd = ed_login_pwd.getText().toString();
                    VerificacionFirebase(email, pwd);
                }
            }
        });


        txt_forgot_pwd = findViewById(R.id.txt_forgot_password);
        txt_forgot_pwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showDialogForgotPwd();
                return false;
            }
        });


    }

    //permisos
    public void permisos() {
        if (ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat
                        .checkSelfPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_CODE);
        } else {
            // Si tiene los permisos
            // verficiar el  checkPlayService
            Log.e(TAG, "si tiene los permisos");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "si tiene los permisos  v2 ");
                }
            }
        }

    }


    //.AUTENTICACION CON FIREBASE
    public void VerificacionFirebase(String usernamelogin, String passwordlogin) {
        Log.e(TAG, " ===========================================================");
        Log.e(TAG, "                VerificacionFirebase");
        waitingDialog.show();
        auth
                .signInWithEmailAndPassword(usernamelogin, passwordlogin)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.e(TAG, " Sign In With Email : success");
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userIUD = firebaseUser.getUid();
                        if (firebaseUser.isEmailVerified()) {
                            updateUI(firebaseUser);
                            goToMain(userIUD);

                        }
                    }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "sign In With Email: Failure  " + e.getMessage());
                        updateUI(null);
                        waitingDialog.dismiss();
                    }
                });

    }

    private void goToMain(String userIUD) {
        FirebaseDatabase
                .getInstance()
                .getReference(Common.TB_INFO_DOCTOR)
                .child(userIUD)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.currentUserDoctor = dataSnapshot.getValue(Usuario.class);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        waitingDialog.dismiss();
                        finish();
                        Log.e("onDataChange : ", "Common.currentUserDoctor : " + Common.currentUserDoctor);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("onCancelled : ", "DatabaseError databaseError = " + databaseError.toString());
                        updateUI(null);
                    }
                });
    }


    //Validación de formulario parte 2
    private boolean checkEmail() {
        if (ed_login_email.getText().toString().trim().isEmpty()) {
            ed_login_email.setError("por favor, ingresar su correo");
            return false;
        }
        return true;
    }

    private boolean checkPassword() {

        if (ed_login_pwd.getText().toString().trim().isEmpty()) {
            ed_login_pwd.setError("por favor, ingresar su contraseña");
            return false;
        }
        return true;
    }

    private boolean submitForm() {

        if (!checkEmail()) {
            ed_login_email.setAnimation(animation);
            ed_login_email.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }
        if (!checkPassword()) {
            ed_login_email.setAnimation(animation);
            ed_login_email.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }
        return true;
    }

    //. Registarse
    public void signup(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            FirebaseUser currentUser = auth.getCurrentUser();
            updateUI(currentUser);
        }

    }

    private void updateUI(FirebaseUser usuarioFirebase) {
        if (usuarioFirebase != null) {
            if (usuarioFirebase.isEmailVerified()) {

            }
        } else {
            Toast.makeText(this, "usuario o contraseña incorrecto", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        managePrefs();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        managePrefs();
    }

    private void managePrefs() {
        if (checkBox.isChecked()) {
            editor.putString(KEY_USERNAME, ed_login_email.getText().toString().trim());
            editor.putString(KEY_PASS, ed_login_pwd.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        } else {
            editor.putBoolean(KEY_REMEMBER, true);
            editor.remove(KEY_PASS);
            editor.remove(KEY_USERNAME);
            editor.apply();
        }
    }

    private void showDialogForgotPwd() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Recuperar Contraseña");
        alertDialog.setMessage("Escriba su correo");

        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View forgot_pwd_layout = inflater.inflate(R.layout.layout_forgot_pwd, null);

        final MaterialEditText editEmail = (MaterialEditText) forgot_pwd_layout.findViewById(R.id.edtEmailForgot);
        alertDialog.setView(forgot_pwd_layout);

        alertDialog.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                final SpotsDialog waitingDialog = new SpotsDialog(LoginActivity.this, R.style.DialogResetearPassword);
                waitingDialog.show();

                auth
                        .sendPasswordResetEmail(editEmail.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialogInterface.dismiss();
                                waitingDialog.dismiss();
                                Log.e(TAG, "");
                                Toast.makeText(LoginActivity.this, "Revise su correo", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialogInterface.dismiss();
                                waitingDialog.dismiss();
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(LoginActivity.this, "Su correo no esta registrado", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

        alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }
}
