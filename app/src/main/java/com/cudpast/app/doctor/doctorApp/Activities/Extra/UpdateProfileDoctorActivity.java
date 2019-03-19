package com.cudpast.app.doctor.doctorApp.Activities.Extra;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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

    private ImageView updateDoctorPhotoView;

    private Button btn_UploadInfoDoctor, btn_chooseDoctorPhoto, btn_UploadPhotoDoctor;

    private DatabaseReference tb_Info_Doctor;

    private Uri mUriPhoto;

    private StorageReference StorageReference;
    private UploadTask uploadTask;

    boolean choosed;
    Usuario usuario;
    String userAuthId;

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
        updateDoctorPhotoView = findViewById(R.id.updateDoctorPhoto);

        btn_UploadInfoDoctor = findViewById(R.id.btnUpdateDoctoAll);
        btn_UploadPhotoDoctor = findViewById(R.id.btn_updateDoctorPhoto);
        btn_chooseDoctorPhoto = findViewById(R.id.btn_chooseDoctorPhoto);


        //.Obtener usuario actualizr
        usuario = Common.currentUser;
        //.Display on XML
        updateDoctorName.setText(usuario.getFirstname());
        updateDoctorLast.setText(usuario.getLastname());
        updateDoctorNumPhone.setText(usuario.getNumphone());
        updateDoctorDir.setText(usuario.getDireccion());
        updateDoctorCodMePe.setText(usuario.getCodmedpe());
        updateDoctorEsp.setText(usuario.getEspecialidad());
        // update XML
        Picasso
                .with(this)
                .load(usuario.getImage())
                .placeholder(R.drawable.ic_photo_doctor)
                .resize(200, 200)
                .error(R.drawable.ic_photo_doctor)
                .into(updateDoctorPhotoView);

        userAuthId = usuario.getUid();
        //todo: que lo haga de manera independiten su propio boton
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


        btn_UploadInfoDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                final Usuario updateUser = new Usuario();
                final SpotsDialog waitingDialog = new SpotsDialog(UpdateProfileDoctorActivity.this, R.style.DialogUpdateDoctorProfile);
                waitingDialog.show();
                String imgPath = Common.currentUser.getImage();

                //.Usuario a actualizar on Firebase
                updateUser.setDni(usuario.getDni());
                updateUser.setFirstname(updateDoctorName.getText().toString());
                updateUser.setLastname(updateDoctorLast.getText().toString());
                updateUser.setNumphone(updateDoctorNumPhone.getText().toString());
                updateUser.setCodmedpe(updateDoctorCodMePe.getText().toString());
                updateUser.setEspecialidad(updateDoctorEsp.getText().toString());
                updateUser.setImage(imgPath);
                updateUser.setDireccion(updateDoctorDir.getText().toString());
                updateUser.setPassword(usuario.getPassword());
                updateUser.setCorreoG(usuario.getCorreoG());
                updateUser.setFecha(usuario.getFecha());
                updateUser.setUid(usuario.getUid());
//                Log.e(TAG, "updatePhotoToStorage" + updateUser.getCadena());
                Common.currentUser = updateUser;


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
            //  btn_UploadPhotoDoctor.setVisibility(View.VISIBLE);
//            Picasso
//                    .with(this)
//                    .load(mUriPhoto)
//                    .transform()
//                    .placeholder(R.drawable.ic_photo_doctor)
//                    .fit()
//                    .error(R.drawable.ic_photo_doctor)
//                    .centerInside()
//                    .into(updateDoctorPhotoView);

            Glide.with(this).load(mUriPhoto).into(updateDoctorPhotoView);
        }

    }
    //Paso 3

    private void updatePhotoToStorage() {

        Log.e(TAG, "updatePhotoToStorage ");
        final SpotsDialog waitingDialog = new SpotsDialog(UpdateProfileDoctorActivity.this, R.style.DialogUpdateDoctorProfile);
        waitingDialog.show();

        String userdni = Common.currentUser.getDni();

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

                                Common.currentUser.setImage(imageUrl);
                                Common.currentUser = updateUser;

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


    //Soporte 1 :ES PARA LA EXTESNION DE LA IMAGEN
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


}