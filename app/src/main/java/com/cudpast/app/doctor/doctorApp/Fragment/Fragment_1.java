package com.cudpast.app.doctor.doctorApp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.Activities.Extra.UpdateProfileDoctorActivity;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.squareup.picasso.Picasso;


public class Fragment_1 extends Fragment {

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView espTextView;
    private TextView telTextView;
    private Button btn_update_doctor;



    private static final String TAG = Fragment_1.class.getSimpleName();

    public Fragment_1() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View f1 = inflater.inflate(R.layout.fragment_1, container, false);
        btn_update_doctor = f1.findViewById(R.id.btn_update_doctor);

        photoImageView = f1.findViewById(R.id.idFotoUsuario);
        nameTextView = f1.findViewById(R.id.nameTextView);
        emailTextView = f1.findViewById(R.id.emailTextView);
        espTextView = f1.findViewById(R.id.espTextView);
        telTextView = f1.findViewById(R.id.telTextView);


        try {
            Usuario usuario = Common.currentUser;
            String name = usuario.getFirstname();
            String email = usuario.getCorreoG();

            Log.e(TAG, " name :" + name);
            Log.e(TAG, " email :" + email);
            nameTextView.setText(usuario.getFirstname());
            emailTextView.setText(usuario.getCorreoG());
            espTextView.setText(usuario.getEspecialidad());
            telTextView.setText(usuario.getNumphone());

            Picasso
                    .with(getActivity())
                    .load(usuario.getImage())
                    .placeholder(R.drawable.ic_photo_doctor)
                    .error(R.drawable.ic_photo_doctor)
                    .into(photoImageView);

        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_update_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() , UpdateProfileDoctorActivity.class);
                startActivity(intent);

            }
        });




        return f1;
    }


}
