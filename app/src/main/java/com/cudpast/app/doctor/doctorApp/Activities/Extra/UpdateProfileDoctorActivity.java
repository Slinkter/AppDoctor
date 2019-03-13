package com.cudpast.app.doctor.doctorApp.Activities.Extra;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class UpdateProfileDoctorActivity extends AppCompatActivity {


    public static final String TAG = UpdateProfileDoctorActivity.class.getSimpleName();

    private TextView updateDoctorName,
            updateDoctorLast,
            updateDoctorNumPhone,
            updateDoctorDir,
            updateDoctorCodMePe,
            updateDoctorEsp;

    private ImageView updateDoctorPhoto;

    private Button btnGuarda;

    private DatabaseReference tb_Info_Doctor;

    //todo : update Photo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_doctor);
        getSupportActionBar().hide();

        tb_Info_Doctor = FirebaseDatabase.getInstance().getReference(Common.TB_INFO_DOCTOR);
        //. XML
        updateDoctorName = findViewById(R.id.updateDoctorName);
        updateDoctorLast = findViewById(R.id.updateDoctorLast);
        updateDoctorNumPhone = findViewById(R.id.updateDoctorNumPhone);
        updateDoctorDir = findViewById(R.id.updateDoctorDir);
        updateDoctorCodMePe = findViewById(R.id.updateDoctorCodMePe);
        updateDoctorEsp = findViewById(R.id.updateDoctorEsp);
        updateDoctorPhoto = findViewById(R.id.updateDoctorPhoto);

        btnGuarda = findViewById(R.id.btnUpdateDoctoAll);

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
                .error(R.drawable.ic_photo_doctor)
                .into(updateDoctorPhoto);

        final String userAuthId = usuario.getUid();
        btnGuarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                updateUser.setImage(usuario.getImage());//<-- set nueva imagen
                updateUser.setUid(usuario.getUid());

                //solo deberia altualizar algunos campos pero esta creadno nuevo
                tb_Info_Doctor
                        .child(userAuthId)
                        .setValue(updateUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, " onSuccess Update Profile Doctor");
                        Common.currentUser = updateUser;
                        iniciarActivity();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Common.currentUser = null;
                        Log.e(TAG, " ERROR :" + e.getMessage());
                    }
                });


            }
        });

    }

    public void iniciarActivity() {
        Intent intent = new Intent(UpdateProfileDoctorActivity.this, DoctorHome.class);
        startActivity(intent);
        finish();
    }

}