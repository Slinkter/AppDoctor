package com.cudpast.app.doctor.doctorApp.Activities.Extra;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class UpdatePwdDoctor extends AppCompatActivity {

    private Button updatePWDDoctor;
    private EditText updateEmailDoctor;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd_doctor);
        getSupportActionBar().setTitle("Cambiar Contraseña");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        updateEmailDoctor = findViewById(R.id.updateEmailDoctor);

        updatePWDDoctor = findViewById(R.id.updatePWDDoctor);
        updatePWDDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SpotsDialog waitingDialog = new SpotsDialog(UpdatePwdDoctor.this);
                waitingDialog.show();

                mAuth.sendPasswordResetEmail(updateEmailDoctor.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                Toast.makeText(UpdatePwdDoctor.this, "Revise su correo", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        waitingDialog.dismiss();
                        Toast.makeText(UpdatePwdDoctor.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }
}
