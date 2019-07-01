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

import com.bumptech.glide.Glide;
import com.cudpast.app.doctor.doctorApp.Activities.MainActivity;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
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

public class UpdatePhotoDoctor extends AppCompatActivity {

    public static final String TAG = UpdatePhotoDoctor.class.getSimpleName();

    private Button btn_chooseDoctorPhoto, btn_UploadPhotoDoctor;
    public static final int PICK_IMAGE_REQUEST = 1;
    private UploadTask uploadTask;
    private DatabaseReference tb_Info_Doctor;
    private StorageReference StorageReference;

    private Uri mUriPhoto;
    Usuario usuario;
    String userAuthId;

    private ImageView updateDoctorPhotoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_photo_doctor);

        getSupportActionBar().setTitle("Actualizar Foto");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tb_Info_Doctor = FirebaseDatabase.getInstance().getReference(Common.TB_INFO_DOCTOR);
        StorageReference = FirebaseStorage.getInstance().getReference("DoctorRegisterApp");
        usuario = Common.currentUserDoctor;
        userAuthId = usuario.getUid();

        updateDoctorPhotoView = findViewById(R.id.updateDoctorPhoto);

        Glide
                .with(this)
                .load(Common.currentUserDoctor.getImage())
                .into(updateDoctorPhotoView);

        btn_UploadPhotoDoctor = findViewById(R.id.btn_updateDoctorPhoto);
        btn_chooseDoctorPhoto = findViewById(R.id.btn_chooseDoctorPhoto);

        btn_chooseDoctorPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btn_UploadPhotoDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePhotoToStorage();
            }
        });

    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //Todo : reducir el tamaño de la imagen o foto
    private void updatePhotoToStorage() {
        Log.e(TAG, "updatePhotoToStorage ");
        final SpotsDialog waitingDialog = new SpotsDialog(UpdatePhotoDoctor.this, R.style.DialogUpdateDoctorProfile);
        waitingDialog.show();

        String userdni = Common.currentUserDoctor.getDni();


        final StorageReference photoRefe = StorageReference.child(userdni + "." + getFileExtension(mUriPhoto));

        try {

            Bitmap bmp = getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), mUriPhoto), 640);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);

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
                                final Usuario updateUser = new Usuario();
                                //.Usuario a actualizar on Firebase
                                updateUser.setDni(usuario.getDni());
                                updateUser.setFirstname(usuario.getFirstname());
                                updateUser.setLastname(usuario.getLastname());
                                updateUser.setNumphone(usuario.getNumphone());
                                updateUser.setCodmedpe(usuario.getCodmedpe());
                                updateUser.setEspecialidad(usuario.getEspecialidad());
                                updateUser.setDireccion(usuario.getDireccion());

                                updateUser.setPassword(usuario.getPassword());
                                updateUser.setCorreoG(usuario.getCorreoG());
                                updateUser.setFecha(usuario.getFecha());
                                updateUser.setImage(imageUrl);//<-- set nueva imagen
                                updateUser.setUid(usuario.getUid());

                                Common.currentUserDoctor.setImage(imageUrl);
                                Common.currentUserDoctor = updateUser;


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

    //Paso 2
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUriPhoto = data.getData();
            btn_UploadPhotoDoctor.setEnabled(true);
            Glide.with(this).load(mUriPhoto).into(updateDoctorPhotoView);
        }

    }
    //Paso 3

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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void iniciarActivity() {
        Intent intent = new Intent(UpdatePhotoDoctor.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
