package com.cudpast.app.doctor.doctorApp.Activities.Extra;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.Activities.LoginActivity;
import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class UpdateProfileDoctorActivity extends AppCompatActivity {


    public static final String TAG = UpdateProfileDoctorActivity.class.getSimpleName();
    public static final int PICK_IMAGE_REQUEST = 1;

    private TextView updateDoctorName,
            updateDoctorLast,
            updateDoctorNumPhone,
            updateDoctorDir,
            updateDoctorCodMePe,
            updateDoctorEsp;

    private ImageView updateDoctorPhoto;

    private Button btnGuarda, btn_updateDoctorPhoto;

    private DatabaseReference tb_Info_Doctor;

    private Uri mUriImage;

    private StorageReference StorageReference;
    private UploadTask uploadTask;

    //todo : agregar Dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_doctor);
        getSupportActionBar().hide();

        tb_Info_Doctor = FirebaseDatabase.getInstance().getReference(Common.TB_INFO_DOCTOR);
        StorageReference = FirebaseStorage.getInstance().getReference("DoctorRegisterApp");
        //. XML
        updateDoctorName = findViewById(R.id.updateDoctorName);
        updateDoctorLast = findViewById(R.id.updateDoctorLast);
        updateDoctorNumPhone = findViewById(R.id.updateDoctorNumPhone);
        updateDoctorDir = findViewById(R.id.updateDoctorDir);
        updateDoctorCodMePe = findViewById(R.id.updateDoctorCodMePe);
        updateDoctorEsp = findViewById(R.id.updateDoctorEsp);
        updateDoctorPhoto = findViewById(R.id.updateDoctorPhoto);

        btnGuarda = findViewById(R.id.btnUpdateDoctoAll);
        btn_updateDoctorPhoto = findViewById(R.id.btn_updateDoctorPhoto);

        //.Obtener usuario actualizr
        final Usuario usuario = Common.currentUser;
        //.Display on XML
        updateDoctorName.setText(usuario.getFirstname());
        updateDoctorLast.setText(usuario.getLastname());
        updateDoctorNumPhone.setText(usuario.getNumphone());
        updateDoctorDir.setText(usuario.getDireccion());
        updateDoctorCodMePe.setText(usuario.getCodmedpe());
        updateDoctorEsp.setText(usuario.getEspecialidad());

        Picasso
                .with(this)
                .load(usuario.getImage())
                .placeholder(R.drawable.ic_photo_doctor)
                .resize(200, 200)
                .error(R.drawable.ic_photo_doctor)
                .into(updateDoctorPhoto);

        final String userAuthId = usuario.getUid();

        btn_updateDoctorPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });



        btnGuarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    final SpotsDialog waitingDialog = new SpotsDialog(UpdateProfileDoctorActivity.this, R.style.DialogUpdateDoctorProfile);
                    waitingDialog.show();

                    String userdni = Common.currentUser.getDni();

                    final StorageReference fileReference = StorageReference.child(userdni + "." + getFileExtension(mUriImage));
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
                                    final Usuario updateUser = new Usuario();

                                    //.Usuario a actualizar on Firebase
                                    updateUser.setDni(usuario.getDni());
                                    updateUser.setFirstname(updateDoctorName.getText().toString());
                                    updateUser.setLastname(updateDoctorLast.getText().toString());
                                    updateUser.setNumphone(updateDoctorNumPhone.getText().toString());
                                    updateUser.setCodmedpe(updateDoctorCodMePe.getText().toString());
                                    updateUser.setEspecialidad(updateDoctorEsp.getText().toString());
                                    updateUser.setDireccion(updateDoctorDir.getText().toString());

                                    updateUser.setPassword(usuario.getPassword());
                                    updateUser.setCorreoG(usuario.getCorreoG());
                                    updateUser.setFecha(usuario.getFecha());
                                    updateUser.setImage(imageUrl);//<-- set nueva imagen
                                    updateUser.setUid(usuario.getUid());

                                    //solo deberia altualizar algunos campos pero esta creadno nuevo
                                    tb_Info_Doctor
                                            .child(userAuthId)
                                            .setValue(updateUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            waitingDialog.dismiss();
                                            Log.e(TAG, " onSuccess Update Profile Doctor");

                                            Common.currentUser = updateUser;
                                            Common.currentUser.setImage(imageUrl);
                                            iniciarActivity();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            waitingDialog.dismiss();
                                            e.printStackTrace();
                                            Common.currentUser = null;
                                            Log.e(TAG, " ERROR :" + e.getMessage());
                                        }
                                    });


                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("RegisterActivity", "Error : -->" + e);
                                }

                            } else {
                                waitingDialog.dismiss();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

//
            }
        });

    }

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

            Picasso.with(this).load(mUriImage).fit().centerInside().into(updateDoctorPhoto);
        }
    }

    //Soporte 1 :ES PARA LA EXTESNION DEL JPG O IMAGEN
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void iniciarActivity() {
        Intent intent = new Intent(UpdateProfileDoctorActivity.this, DoctorHome.class);
        startActivity(intent);
        finish();
    }

}