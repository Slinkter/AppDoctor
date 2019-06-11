package com.cudpast.app.doctor.doctorApp.Activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.Soporte.Token;
import com.cudpast.app.doctor.doctorApp.Soporte.VolleyRP;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {
    // todo : no carga el alert o Dialog para que espere el usuarios cuando se registrar
    public static final String TAG = RegisterActivity.class.getSimpleName();
    public static final int PICK_IMAGE_REQUEST = 1;
    private RequestQueue mRequest;
    private VolleyRP volleyRP;
    private EditText signupDNI, signupName, signupLast, signupNumPhone, signupCodMePe, signupEsp, signupMail, signupAnddress, signupPassword;
    private Button btn_save, btn_uploadPhoto;
    private Animation animation;
    private Vibrator vib;
    private ImageView signupImagePhoto;
    private Uri uriPhoto;
    private UploadTask uploadTask;

    private FirebaseAuth auth;
    private DatabaseReference db_doctor_consulta;
    private DatabaseReference tb_Info_Doctor;
    private StorageReference StorageReference;

    SpotsDialog waitingDialog;
    StorageReference fileReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().hide();

        btn_save = findViewById(R.id.btnGuardar);
        btn_uploadPhoto = findViewById(R.id.btn_choose_image);

        volleyRP = VolleyRP.getInstance(this);
        mRequest = volleyRP.getRequestQueue();

        //Firebase init
        auth = FirebaseAuth.getInstance();
        //Almacenar info del nuevo Doctor registrado
        tb_Info_Doctor = FirebaseDatabase.getInstance().getReference(Common.TB_INFO_DOCTOR);
        //Para consulta offline del doctor registado
        db_doctor_consulta = FirebaseDatabase.getInstance().getReference();
        //Almacenar la foto del nuevo doctor
        StorageReference = FirebaseStorage.getInstance().getReference("DoctorRegisterApp");
        //Animación de error al ingresar dato en el formulario
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        signupName = findViewById(R.id.signupName);
        signupLast = findViewById(R.id.signupLast);
        signupNumPhone = findViewById(R.id.signupNumPhone);
        signupAnddress = findViewById(R.id.signupDir);
        signupCodMePe = findViewById(R.id.signupCodMePe);
        signupEsp = findViewById(R.id.signupEsp);
        signupMail = findViewById(R.id.signupMail);
        signupDNI = findViewById(R.id.signupDNI);
        signupPassword = findViewById(R.id.signupPassword);
        signupImagePhoto = findViewById(R.id.image_view);


        btn_uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String firstname = signupName.getText().toString();
                final String lastname = signupLast.getText().toString();
                final String numphone = signupNumPhone.getText().toString();
                final String direccion = signupAnddress.getText().toString();
                final String codmedpe = signupCodMePe.getText().toString();
                final String especialidad = signupEsp.getText().toString();
                final String dni = signupDNI.getText().toString();

                final String fecha = getCurrentTimeStamp();

                // Validar Formulario
                if (submitForm()) {

                    // Validar foto
                    if (uriPhoto != null) {

                        waitingDialog = new SpotsDialog(RegisterActivity.this, R.style.DialogRegistro);
                        waitingDialog.show();

                        fileReference = StorageReference.child(dni + "." + getFileExtension(uriPhoto));
                        uploadTask = fileReference.putFile(uriPhoto);

                        uploadTask
                                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw Objects.requireNonNull(task.getException());
                                        }
                                        return fileReference.getDownloadUrl();
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            try {

                                                Uri downloadUri = task.getResult();
                                                final String imageUrl = downloadUri.toString();
                                                //Guardar en firebase
                                                String email = signupMail.getText().toString().trim();
                                                String password = signupPassword.getText().toString().trim();
                                                //
                                                auth
                                                        .createUserWithEmailAndPassword(email, password)
                                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                            @Override
                                                            public void onSuccess(AuthResult authResult) {

                                                                String uid = authResult.getUser().getUid();
                                                                Usuario FirebaseUser = new Usuario(dni, firstname, lastname, numphone, codmedpe, especialidad, direccion, "", signupMail.getText().toString().trim(), fecha, imageUrl, uid);

                                                                tb_Info_Doctor.
                                                                        child(uid)
                                                                        .setValue(FirebaseUser)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                sendEmailVerification();
                                                                                Log.e(TAG, " onSuccess ");
                                                                                Toast.makeText(RegisterActivity.this, "Usuario Registrado , espere correo de verificación", Toast.LENGTH_SHORT).show();
                                                                                Usuario user3 = new Usuario(dni, firstname, lastname, numphone, especialidad, imageUrl);
                                                                                db_doctor_consulta.child("db_doctor_consulta").child(dni).setValue(user3);

                                                                                waitingDialog.dismiss();
                                                                                iniciarActivity();

                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {

                                                                                Toast.makeText(RegisterActivity.this, "Usuario  No Registrado ", Toast.LENGTH_SHORT).show();
                                                                                Log.e("RegisterActivity ", "onFailure ");
                                                                                waitingDialog.dismiss();
                                                                            }
                                                                        });

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                waitingDialog.dismiss();
                                                                Toast.makeText(RegisterActivity.this, "Fallo de Internet  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Log.e("RegisterActivity", "Error : -->" + e);
                                            }

                                        } else {

                                        }
                                    }

                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Error : -->" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    //waitingDialog.dismiss();
                    //iniciarActivity();
                }
            }
        });
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

    private void sendEmailVerification() {

        final FirebaseUser user = auth.getCurrentUser();
        user
                .sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Revise su correo " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        //onActivityResult
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriPhoto = data.getData();
            Glide
                    .with(this)
                    .load(uriPhoto)
                    .into(signupImagePhoto);
        }
    }

    //ES PARA LA EXTESNION DE LA IMAGEN
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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
            signupAnddress.setAnimation(animation);
            signupAnddress.startAnimation(animation);
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
        if (signupAnddress.getText().toString().trim().isEmpty()) {
            signupAnddress.setError("Error ingresar direccion");
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


    public void generarToken() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tbl);

        Token token = new Token(refreshedToken);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            tokens
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);
            Common.token_doctor = token.getToken();
            Log.e("TOKEN : ", refreshedToken);
        }
    }

}
