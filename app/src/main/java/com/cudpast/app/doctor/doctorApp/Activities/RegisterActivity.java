package com.cudpast.app.doctor.doctorApp.Activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.DoctorProfile;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Soporte.Token;
import com.cudpast.app.doctor.doctorApp.Soporte.VolleyRP;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = RegisterActivity.class.getSimpleName();
    public static final int PICK_IMAGE_REQUEST = 1;

    private EditText signupDNI, signupFirstName, signupLastName, signupNumPhone, signupCodMePe, signupEsp, signupMail, signupAnddress, signupPassword;
    private RequestQueue mRequest;
    private VolleyRP volleyRP;

    private Button btnNewDoctor ,btnExitDoctor, btn_uploadPhoto;
    private Animation animation;
    private Vibrator vib;
    private ImageView signupImagePhoto;
    private Uri uriPhoto;
    private UploadTask uploadTaskPhoto;

    private FirebaseAuth auth;

    private DatabaseReference tb_Info_Doctor;
    private DatabaseReference tb_Info_Plasma;
    private StorageReference StorageReference;

    SpotsDialog waitingDialog;
    StorageReference fileReference;
    Spinner spinner;
    String dni, firstname, lastname, numphone, codmedpe, especialidad, address, password, email, dateSuscriptor, imageUrl, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().hide();
        //
        Toolbar toolbar = findViewById(R.id.toolbarRegistro);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("");
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //
        btnNewDoctor = findViewById(R.id.btnNewDoctor);
        btnExitDoctor = findViewById(R.id.btnExitDoctor);
        btn_uploadPhoto = findViewById(R.id.btn_choose_image);
        spinner = findViewById(R.id.signupSpinnerCategoria);

        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(this, R.array.categoria, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        volleyRP = VolleyRP.getInstance(this);
        mRequest = volleyRP.getRequestQueue();

        //Firebase init
        auth = FirebaseAuth.getInstance();
        //Almacenar info del nuevo Doctor registrado
        tb_Info_Doctor = FirebaseDatabase.getInstance().getReference(Common.TB_INFO_DOCTOR);
        //Almacenar info del nuevo Plasma registrado
        tb_Info_Plasma = FirebaseDatabase.getInstance().getReference(Common.TB_INFO_PLASMA);

        //Almacenar la foto del nuevo doctor
        StorageReference = FirebaseStorage.getInstance().getReference("DoctorRegisterApp");
        //Animación de error al ingresar dato en el formulario
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //xml
        signupFirstName = findViewById(R.id.signupName);
        signupLastName = findViewById(R.id.signupLast);
        signupNumPhone = findViewById(R.id.signupNumPhone);
        signupAnddress = findViewById(R.id.signupDir);
        signupCodMePe = findViewById(R.id.signupCodMePe);
        signupMail = findViewById(R.id.signupMail);
        signupPassword = findViewById(R.id.signupPassword);
        signupDNI = findViewById(R.id.signupDNI);
        signupImagePhoto = findViewById(R.id.image_view);

        signupEsp = findViewById(R.id.signupEsp); //<-- borrar spinner = findViewById(R.id.signupSpinnerCategoria);
        //
        waitingDialog = new SpotsDialog(RegisterActivity.this, R.style.DialogRegistro);

        btn_uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnNewDoctor
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (submitForm()) {
                            //Selected Photo
                            if (uriPhoto != null) {
                                waitingDialog.show();
                                //insertar photo en Storage
                                fileReference = StorageReference.child(dni + "." + getFileExtension(uriPhoto));
                                uploadTaskPhoto = fileReference.putFile(uriPhoto);
                                uploadTaskPhoto
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
                                                    Uri downloadUri = task.getResult();
                                                    imageUrl = downloadUri.toString();
                                                    CreateDoctoronFirebase();
                                                }
                                            }

                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterActivity.this, "No se inserto la foto en la Storage : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                });

        btnExitDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void CreateDoctoronFirebase() {
        Log.e(TAG, " ======================");
        Log.e(TAG, " ----> CreateDoctoronFirebase ");
        email = signupMail.getText().toString().trim();
        password = signupPassword.getText().toString().trim();
        dni = signupDNI.getText().toString();
        firstname = signupFirstName.getText().toString();
        lastname = signupLastName.getText().toString();
        numphone = signupNumPhone.getText().toString();
        codmedpe = signupCodMePe.getText().toString();
        address = signupAnddress.getText().toString();
        dateSuscriptor = getCurrentTimeStamp();
        //
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.e(TAG, " onSuccess : New Doctor created");
                        uid = authResult.getUser().getUid();
                        especialidad = spinner.getSelectedItem().toString();// plasma o medico general
                        DoctorProfile newDoctor = new DoctorProfile(uid, imageUrl, firstname, lastname, numphone, address, codmedpe, dni, email, password, especialidad, dateSuscriptor);
                        SaveNewDoctorOnFirebase(newDoctor);
                        waitingDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, " onFaile:  It wasn´t created  = " + e.getMessage());
                        Toast.makeText(RegisterActivity.this, "No se creo el usuario " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        waitingDialog.dismiss();
                    }
                });
    }

    private void SaveNewDoctorOnFirebase(DoctorProfile newDoctor) {
        Log.e(TAG, " ======================");
        Log.e(TAG, " ----> SaveNewDoctorOnFirebase ");
        tb_Info_Doctor
                .child(uid)
                .setValue(newDoctor)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, " onSuccess : save doctor  on tb_Info_Doctor");
                        generarToken();
                        sendEmailVerification();
                        iniciarActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "DoctorProfile  No Registrado ", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure  : not save doctor  on tb_Info_Doctor ");
                    }
                });
    }

    private void insertarNewPlasma(DoctorProfile newUser) {
        DoctorProfile newPlasma = newUser;
        tb_Info_Plasma
                .child(uid)
                .setValue(newPlasma)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //
                        sendEmailVerification();
                        generarToken();
                        //
                        Log.e(TAG, " onSuccess : insertNewPlasma");
                        Toast.makeText(RegisterActivity.this, "Verifique su correo, por favor", Toast.LENGTH_SHORT).show();
                        //
                        iniciarActivity();
                        waitingDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "DoctorProfile  No Registrado ", Toast.LENGTH_SHORT).show();
                        Log.e("RegisterActivity ", "onFailure ");
                        waitingDialog.dismiss();
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

        auth.getCurrentUser()
                .sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Verifique su correo, por favor", Toast.LENGTH_SHORT).show();
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
        //-->onActivityResult
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


        if (!checkName()) {
            signupFirstName.setAnimation(animation);
            signupFirstName.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkLast()) {
            signupLastName.setAnimation(animation);
            signupLastName.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkNumPhone()) {
            signupNumPhone.setAnimation(animation);
            signupNumPhone.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }
        // address
        if (!checkAnddress()) {
            signupAnddress.setAnimation(animation);
            signupAnddress.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkCodMe()) {
            signupCodMePe.setAnimation(animation);
            signupCodMePe.startAnimation(animation);
            vib.vibrate(120);
            return false;
        }

        if (!checkDNI()) {
            signupDNI.setAnimation(animation);
            signupDNI.startAnimation(animation);
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
        if (signupFirstName.getText().toString().trim().isEmpty()) {
            signupFirstName.setError("Error ingresar nombre");
            return false;
        }
        return true;
    }

    private boolean checkLast() {
        if (signupLastName.getText().toString().trim().isEmpty()) {
            signupLastName.setError("Error ingresar apellido");
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

    private boolean checkAnddress() {
        if (signupAnddress.getText().toString().trim().isEmpty()) {
            signupAnddress.setError("Error ingresar address");
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


    private boolean checkEmail() {
        if (signupMail.getText().toString().trim().isEmpty()) {
            signupMail.setError("Error ingresar correo ");
            return false;
        }
        return true;
    }


    private boolean checkPassword() {
        if (signupPassword.getText().toString().trim().isEmpty()) {
            signupPassword.setError("Error ingresar contraseña");
            return false;
        }
        return true;
    }

    public void generarToken() {
        Log.e(TAG, " ======================");
        Log.e(TAG, " ----> generarToken ");
        String newToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference refDB_tokens = db.getReference(Common.token_tbl);

        Token token = new Token(newToken);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String doctorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            refDB_tokens
                    .child(doctorUID)
                    .setValue(token);
            Common.token_doctor = token.getToken();
            Log.e(TAG ,"newToken = " + newToken) ;
        }
    }

    // Validando Token
    public void generarToken2() {
        // Get token
        // [START retrieve_current_token]
        Log.e(TAG, "generarToken2()");
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = "token" + token;
                        Log.e(TAG, msg);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END retrieve_current_token]
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        //  Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
        Log.e(TAG, text);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
