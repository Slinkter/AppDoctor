package com.cudpast.app.doctor.doctorApp.Activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Vibrator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONException;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.User;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.Soporte.VolleyRP;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String IP_REGISTRAR = "http://www.cudpast.com/AppDoctor/Registro_INSERT.php";
    public static final int PICK_IMAGE_REQUEST = 1;
    private RequestQueue mRequest;
    private VolleyRP volleyRP;
    private EditText signupDNI, signupName, signupLast, signupNumPhone, signupCodMePe, signupEsp, signupMail, signupDir, signupPassword;
    private Button guardar, salir, uploadPhoto;
    private Animation animation;
    private Vibrator vib;
    private ImageView signupImagePhoto;
    private Uri mUriImage;
    private UploadTask uploadTask;

    private DatabaseReference databaseReference;
    private DatabaseReference tb_Info_Doctor;
    private StorageReference StorageReference;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().hide();

        guardar = findViewById(R.id.btnGuardar);
        salir = findViewById(R.id.btnSalir);
        uploadPhoto = findViewById(R.id.btn_choose_image);

        volleyRP = VolleyRP.getInstance(this);
        mRequest = volleyRP.getRequestQueue();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        StorageReference = FirebaseStorage.getInstance().getReference("DoctorRegisterApp");
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        //Firebase init
        auth = FirebaseAuth.getInstance();
        tb_Info_Doctor = FirebaseDatabase.getInstance().getReference(Common.tb_Info_Doctor);

        signupName = findViewById(R.id.signupName);
        signupLast = findViewById(R.id.signupLast);
        signupNumPhone = findViewById(R.id.signupNumPhone);
        signupDir = findViewById(R.id.signupDir); //<--- dirección
        signupCodMePe = findViewById(R.id.signupCodMePe);
        signupEsp = findViewById(R.id.signupEsp);
        signupMail = findViewById(R.id.signupMail);
        signupDNI = findViewById(R.id.signupDNI);
        signupPassword = findViewById(R.id.signupPassword);
        signupImagePhoto = findViewById(R.id.image_view);


        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String firstname = signupName.getText().toString();
                final String lastname = signupLast.getText().toString();
                final String numphone = signupNumPhone.getText().toString();
                final String direccion = signupDir.getText().toString(); // <--- dirección
                final String codmedpe = signupCodMePe.getText().toString();
                final String especialidad = signupEsp.getText().toString();
                final String dni = signupDNI.getText().toString();// <-- 123456789@doctor.com
                final String password = signupPassword.getText().toString();
                final String mail = signupMail.getText().toString();
                final String fecha = getCurrentTimeStamp();

                // Validar Formulario
                if (submitForm()) {
                    final SpotsDialog waitingDialog = new SpotsDialog(RegisterActivity.this, R.style.DialogRegistro);
                    waitingDialog.show();
                    // Validar foto
                    if (mUriImage != null) {

                        final StorageReference fileReference = StorageReference.child(dni + "." + getFileExtension(mUriImage));
                        uploadTask = fileReference.putFile(mUriImage);

                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                return fileReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    try {
                                        Uri downloadUri = task.getResult();
                                        final String imageUrl = downloadUri.toString();
                                        //Base de datos : Archivo Php
                                        if (registrarWebGoDaddy(dni, firstname, lastname, numphone, codmedpe, especialidad, direccion, password, mail, fecha)) {
                                            //Base de datos : Firebase

                                            Usuario user1 = new Usuario(dni, firstname, lastname, numphone, codmedpe, especialidad, direccion, password, mail, fecha, imageUrl);
                                            Usuario user2 = new Usuario(dni, password);
                                            Usuario user3 = new Usuario(dni, firstname, lastname, numphone, especialidad, imageUrl);

                                            databaseReference.child("db_doctor_register").child(dni).setValue(user1);
                                            databaseReference.child("db_doctor_login").child(dni).setValue(user2);
                                            databaseReference.child("db_doctor_consulta").child(dni).setValue(user3);

                                            //Crear correo en firebase
                                            auth.createUserWithEmailAndPassword(mail, password)
                                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                        @Override
                                                        public void onSuccess(AuthResult authResult) {
                                                            Log.e("RegisterActivity", "authResult.getUser().getUid()" + authResult.getUser().getUid());
                                                            Log.e("RegisterActivity", "mAuth.getCurrentUser() " + auth.getCurrentUser().toString());
                                                            Log.e("RegisterActivity", "Correo y password : " + mail + password);

                                                            final Usuario FirebaseUser = new Usuario(dni, firstname, lastname, numphone, codmedpe, especialidad, direccion, password, mail, fecha, imageUrl);
                                                            tb_Info_Doctor.
                                                                    child(authResult.getUser().getUid())
                                                                    .setValue(FirebaseUser)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
//
                                                                            Log.e("RegisterActivity ", "onSuccess ");
                                                                            Log.e("RegisterActivity ", "user1 " + FirebaseUser);

                                                                            sendEmailVerification();

                                                                            waitingDialog.dismiss();

                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            waitingDialog.dismiss();
                                                                            Log.e("RegisterActivity ", "onFailure ");


                                                                        }
                                                            });


                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    waitingDialog.dismiss();
                                                    Toast.makeText(RegisterActivity.this, "Fallo de Internet", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {

                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("RegisterActivity", "Error : -->" + e);
                                    }

                                } else {

                                }
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "Error : -->" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    waitingDialog.dismiss();
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
    public Boolean registrarWebGoDaddy(String dni, String firstname, String lastname, String numphone, String codmedpe, String especialidad, String direccion, String password, String correoG, String fecha) {

        boolean registro = false;

        HashMap<String, String> hashMapRegistro = new HashMap<>();
        hashMapRegistro.put("idDNI", dni);
        hashMapRegistro.put("nombre", firstname);
        hashMapRegistro.put("apellido", lastname);
        hashMapRegistro.put("telefono", numphone);
        hashMapRegistro.put("codMedico", codmedpe);
        hashMapRegistro.put("especialidad", especialidad);
        hashMapRegistro.put("direccion", direccion);
        hashMapRegistro.put("password", password);
        hashMapRegistro.put("correo", correoG);
        hashMapRegistro.put("fecha", fecha);

        JsonObjectRequest solicitar = new JsonObjectRequest(
                Request.Method.POST,
                IP_REGISTRAR,
                new JSONObject(hashMapRegistro),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject datos) {

                        try {
                            String estado = datos.getString("resultado");

                            if (estado.equalsIgnoreCase("Datos registrados  :) ")) {
//                                Toast.makeText(RegisterActivity.this, "", Toast.LENGTH_SHORT).show();
                                Log.e("registrarWebGoDaddy", "ok");
                            } else {
//                                Toast.makeText(RegisterActivity.this, estado, Toast.LENGTH_SHORT).show();
                                Log.e("registrarWebGoDaddy", "error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(RegisterActivity.this, "no se pudo registrar", Toast.LENGTH_SHORT).show();
            }
        });


        try {
            VolleyRP.addToQueue(solicitar, mRequest, this, volleyRP);
            registro = true;
            Log.e("registrarWebGoDaddy", "try ok");
        } catch (Exception e) {
            Log.e("registrarWebGoDaddy", "cathc " + e.getMessage());
            e.printStackTrace();
            registro = false;
        }


        return registro;

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
        // direccion
        if (!checkUser()) {
            signupDir.setAnimation(animation);
            signupDir.startAnimation(animation);
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

    // direccion
    private boolean checkUser() {
        if (signupDir.getText().toString().trim().isEmpty()) {
            signupDir.setError("Error ingresar direccion");
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

    private boolean checkDNI() {
        if (signupDNI.length() < 8) {
            signupDNI.setError("Error : ingresar  8 digitos");
            return false;
        }
        return true;
    }


    //Paso 1
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //Paso 2
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUriImage = data.getData();

            Picasso.with(this).load(mUriImage).fit().centerInside().into(signupImagePhoto);
        }
    }

    //Soporte 1 :ES PARA LA EXTESNION DEL JPG O IMAGEN
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void sendEmailVerification() {

        final FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Revise su correo " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("RegisterActivity", "sendEmailVerification", task.getException());
                            Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


}
