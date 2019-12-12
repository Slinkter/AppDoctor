package com.cudpast.app.doctor.doctorApp.Activities.Extra;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.cudpast.app.doctor.doctorApp.Activities.MainActivity;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.DoctorProfile;
import com.cudpast.app.doctor.doctorApp.R;
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

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class UpdateProfileDoctor extends AppCompatActivity {


    public static final String TAG = UpdateProfileDoctor.class.getSimpleName();
    public static final int PICK_IMAGE_REQUEST = 1;

    private TextView updateDoctorName,
            updateDoctorLast,
            updateDoctorNumPhone,
            updateDoctorDir,
            updateDoctorCodMePe,
            updateDoctorEsp;

    private ImageView updateDoctorPhotoView;

    private Button btn_UploadInfoDoctor, btn_chooseDoctorPhoto, btn_UploadPhotoDoctor;

    private DatabaseReference tb_Info_Doctor;

    private Uri mUriPhoto;

    private StorageReference StorageReference;
    private UploadTask uploadTask;

    boolean choosed;
    DoctorProfile doctorProfile;
    String userAuthId;

    //todo : agregar Dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_doctor);
        getSupportActionBar().setTitle("Actualizar datos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tb_Info_Doctor = FirebaseDatabase.getInstance().getReference(Common.TB_INFO_DOCTOR);
        StorageReference = FirebaseStorage.getInstance().getReference("DoctorRegisterApp");
        //. XML
        updateDoctorName = findViewById(R.id.updateDoctorName);
        updateDoctorLast = findViewById(R.id.updateDoctorLast);
        updateDoctorNumPhone = findViewById(R.id.updateDoctorNumPhone);
        updateDoctorDir = findViewById(R.id.updateDoctorDir);
        updateDoctorCodMePe = findViewById(R.id.updateDoctorCodMePe);
        updateDoctorEsp = findViewById(R.id.updateDoctorEsp);
        updateDoctorPhotoView = findViewById(R.id.updateDoctorPhoto);

        btn_UploadInfoDoctor = findViewById(R.id.btnUpdateDoctoAll);


        //.Obtener doctorProfile actualizr
        doctorProfile = Common.currentUserDoctor;
        //.Display on XMLc
        updateDoctorName.setText(doctorProfile.getFirstname());
        updateDoctorLast.setText(doctorProfile.getLastname());
        updateDoctorNumPhone.setText(doctorProfile.getNumphone());
        updateDoctorDir.setText(doctorProfile.getDireccion());
        updateDoctorCodMePe.setText(doctorProfile.getCodmedpe());
        updateDoctorEsp.setText(doctorProfile.getEspecialidad());
        // update XML
//        Picasso
//                .with(this)
//                .load(doctorProfile.getImagePhoto())
//                .placeholder(R.drawable.ic_photo_doctor)
//                .resize(200, 200)
//                .error(R.drawable.ic_photo_doctor)
//                .into(updateDoctorPhotoView);

        userAuthId = doctorProfile.getUid();
        //todo: que lo haga de manera independiten su propio boton


        btn_UploadInfoDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                final DoctorProfile updateUser = new DoctorProfile();
                final SpotsDialog waitingDialog = new SpotsDialog(UpdateProfileDoctor.this, R.style.DialogUpdateDoctorProfile);
                waitingDialog.show();
                String imgPath = Common.currentUserDoctor.getImagePhoto();

                //.DoctorProfile a actualizar on Firebase
                updateUser.setDni(doctorProfile.getDni());
                updateUser.setFirstname(updateDoctorName.getText().toString());
                updateUser.setLastname(updateDoctorLast.getText().toString());
                updateUser.setNumphone(updateDoctorNumPhone.getText().toString());
                updateUser.setCodmedpe(updateDoctorCodMePe.getText().toString());
                updateUser.setEspecialidad(updateDoctorEsp.getText().toString());
                updateUser.setImagePhoto(imgPath);
                updateUser.setDireccion(updateDoctorDir.getText().toString());
                updateUser.setPassword(doctorProfile.getPassword());
                updateUser.setMail(doctorProfile.getMail());
                updateUser.setCreateDate(doctorProfile.getCreateDate());
                updateUser.setUid(doctorProfile.getUid());
//                Log.e(TAG, "updatePhotoToStorage" + updateUser.getCadena());
                Common.currentUserDoctor = updateUser;


                //.Actualizar campos
                tb_Info_Doctor
                        .child(userAuthId)
                        .setValue(updateUser)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                waitingDialog.dismiss();
                                Log.e(TAG, " onSuccess Update Profile Doctor");
                                iniciarActivity();
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                e.printStackTrace();
                            }
                        });


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
            mUriPhoto = data.getData();
            choosed = true;
            btn_UploadPhotoDoctor.setEnabled(true);
            Glide.with(this).load(mUriPhoto).into(updateDoctorPhotoView);
        }

    }

    //.
    private void updatePhotoToStorage() {

        Log.e(TAG, "updatePhotoToStorage ");
        final SpotsDialog waitingDialog = new SpotsDialog(UpdateProfileDoctor.this, R.style.DialogUpdateDoctorProfile);
        waitingDialog.show();

        String userdni = Common.currentUserDoctor.getDni();

        final StorageReference photoRefe = StorageReference.child(userdni + "." + getFileExtension(mUriPhoto));
        //Todo : reducir el tamaño de la imagen o foto
        try {

            Bitmap bmp = getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), mUriPhoto), 60);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);

            byte[] data = baos.toByteArray();

            uploadTask = photoRefe.putBytes(data);//mUriPhoto --> es un URL
            uploadTask
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return photoRefe.getDownloadUrl();

                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                final String imageUrl = downloadUri.toString();
                                final DoctorProfile updateUser = new DoctorProfile();
                                //.DoctorProfile a actualizar on Firebase
                                updateUser.setDni(doctorProfile.getDni());
                                updateUser.setFirstname(updateDoctorName.getText().toString());
                                updateUser.setLastname(updateDoctorLast.getText().toString());
                                updateUser.setNumphone(updateDoctorNumPhone.getText().toString());
                                updateUser.setCodmedpe(updateDoctorCodMePe.getText().toString());
                                updateUser.setEspecialidad(updateDoctorEsp.getText().toString());
                                updateUser.setDireccion(updateDoctorDir.getText().toString());

                                updateUser.setPassword(doctorProfile.getPassword());
                                updateUser.setMail(doctorProfile.getMail());
                                updateUser.setCreateDate(doctorProfile.getCreateDate());
                                updateUser.setImagePhoto(imageUrl);//<-- set nueva imagen
                                updateUser.setUid(doctorProfile.getUid());

                                Common.currentUserDoctor.setImagePhoto(imageUrl);
                                Common.currentUserDoctor = updateUser;

//                                Log.e(TAG, "updatePhotoToStorage : " + updateUser.getCadena());

                                //solo deberia altualizar algunos campos pero esta creadno nuevo
                                tb_Info_Doctor
                                        .child(userAuthId)
                                        .setValue(updateUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                waitingDialog.dismiss();
                                                Log.e(TAG, " onSuccess Update Profile Doctor");

                                                iniciarActivity();

                                            }

                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                waitingDialog.dismiss();
                                            }
                                        });

                            }
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //.ES PARA LA EXTESNION DE LA IMAGEN
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //.
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    //.
    public void iniciarActivity() {
        Intent intent = new Intent(UpdateProfileDoctor.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}