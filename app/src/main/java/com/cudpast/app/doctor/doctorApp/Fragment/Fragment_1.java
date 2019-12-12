package com.cudpast.app.doctor.doctorApp.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.DoctorProfile;
import com.cudpast.app.doctor.doctorApp.R;


public class Fragment_1 extends Fragment {

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView espTextView;
    private TextView lastTextView;
    private Button btn_update_doctor;

    private static final String TAG = Fragment_1.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View f1 = inflater.inflate(R.layout.fragment_1, container, false);

        photoImageView = f1.findViewById(R.id.idFotoUsuario);
        nameTextView = f1.findViewById(R.id.nameTextView);
        emailTextView = f1.findViewById(R.id.emailTextView);
        espTextView = f1.findViewById(R.id.espTextView);
        lastTextView = f1.findViewById(R.id.lastTextView);

        try {
            DoctorProfile doctorProfile = Common.currentUserDoctor;

            nameTextView.setText(doctorProfile.getFirstname());
            emailTextView.setText(doctorProfile.getMail());
            espTextView.setText(doctorProfile.getEspecialidad());
            lastTextView.setText(doctorProfile.getLastname());

            Glide
                    .with(f1.getContext())
                    .load(doctorProfile.getImagePhoto())
                    .into(photoImageView);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return f1;
    }
}
