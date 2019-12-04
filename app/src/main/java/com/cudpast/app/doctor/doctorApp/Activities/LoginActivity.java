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

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Soporte.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity implements TextWatcher, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "LoginActivity";
    //
    private Button btnIngresar;
    private Animation error_anim;
    private Vibrator error_vib;
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
    private static final int MY_PERMISSION_REQUEST_CODE_LOCATION = 7000;
    SpotsDialog waitingDialog;
    private TextView txt_forgot_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        permisos();
        // Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
        // startActivity(intent);
        //--------- FIREBASE INIT-----------
        auth = FirebaseAuth.getInstance();
        //
        waitingDialog = new SpotsDialog(LoginActivity.this, R.style.DialogLogin);
        error_anim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
        error_vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        btnIngresar = findViewById(R.id.btnLogin);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitForm()) {
                    String email = ed_login_email.getText().toString();
                    String pwd = ed_login_pwd.getText().toString();
                    loginFirebase(email, pwd);
                }
            }
        });
        //--------- Start Check-----------
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

        //--------- Recovery Password-----------
        txt_forgot_pwd = findViewById(R.id.txt_forgot_password);
        txt_forgot_pwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                displayRecoveryPassowrd();
                return false;
            }
        });
    }

    // *********************************************************
    //.Permisos de Location
    public void permisos() {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            aux_solicitarPermiso(); // No tiene permisos y solicitar permisos
        } else {
            Log.e(TAG, "permisos : si tiene los permisos");  // Si tiene los permisos
        }
    }

    private void aux_solicitarPermiso() {
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_CODE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE_LOCATION: {
                if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    Log.e(TAG, "onRequestPermissionsResult : si tiene los permisos  v2 ");
                }
            }
        }
    }

    // *********************************************************
    //. Init
    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            FirebaseUser currentUser = auth.getCurrentUser();
            updateUI(currentUser);
        }

    }

    // *********************************************************
    //. Verification  User
    private void updateUI(FirebaseUser usuarioFirebase) {
        if (usuarioFirebase != null) {

        } else {
            Toast.makeText(this, "usuario o contraseña incorrecto", Toast.LENGTH_LONG).show();
        }
    }

    // *********************************************************
    //. Auth - Firebase
    public void loginFirebase(String usernamelogin, String passwordlogin) {
        Log.e(TAG, " ===========================================================");
        Log.e(TAG, "                loginFirebase");
        waitingDialog.show();
        auth
                .signInWithEmailAndPassword(usernamelogin, passwordlogin)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            if (firebaseUser.isEmailVerified()) {
                                updateUI(firebaseUser);
                                goToMain(firebaseUser.getUid());
                                Log.e(TAG, " Sign In With Email : success");
                                waitingDialog.dismiss();
                                Log.e(TAG, "updateUI : correo  verificado(opcional)");
                            }else{
                                waitingDialog.dismiss();
                                Log.e(TAG, "no se ha verificado correo");
                                Toast.makeText(LoginActivity.this, "Falta verificar correo", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Log.e(TAG, "sign In With Email: Failure  " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "usuario o contraseña incorreo", Toast.LENGTH_SHORT).show();
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
                        //
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        //
                        updateTokenToServer(FirebaseInstanceId.getInstance().getToken());
                        Common.currentUserDoctor = dataSnapshot.getValue(Usuario.class);
                        Log.e(TAG, "Common.currentUserDoctor : " + Common.currentUserDoctor);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        waitingDialog.dismiss();
                        Log.e(TAG, "DatabaseError databaseError = " + databaseError.toString());
                        updateUI(null);
                    }
                });
    }

    private void updateTokenToServer(String refreshedToken) {
        Token token = new Token(refreshedToken);
        DatabaseReference db_tokens = FirebaseDatabase.getInstance().getReference(Common.token_tbl);
        if (auth.getCurrentUser() != null) {
            db_tokens
                    .child(auth.getCurrentUser().getUid())
                    .setValue(token);
        }
    }

    // *********************************************************
    // .Validación de formulario
    private boolean submitForm() {

        if (!checkEmail()) {
            ed_login_email.setAnimation(error_anim);
            ed_login_email.startAnimation(error_anim);
            error_vib.vibrate(120);
            return false;
        }
        if (!checkPassword()) {
            ed_login_email.setAnimation(error_anim);
            ed_login_email.startAnimation(error_anim);
            error_vib.vibrate(120);
            return false;
        }
        return true;
    }

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

    // *********************************************************
    //. Registarse
    public void signup(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    // *********************************************************
    //. Check out Username & Password

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        managePrefs();
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
    // *********************************************************
    //. Recovery Username & Password

    private void displayRecoveryPassowrd() {
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
