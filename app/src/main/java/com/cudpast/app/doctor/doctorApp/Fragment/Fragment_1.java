package com.cudpast.app.doctor.doctorApp.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;


public class Fragment_1 extends Fragment {

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView espTextView;
    private TextView lastTextView;
    private Button btn_update_doctor;

    private static final String TAG = Fragment_1.class.getSimpleName();

    public Fragment_1() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View f1 = inflater.inflate(R.layout.fragment_1, container, false);


        photoImageView = f1.findViewById(R.id.idFotoUsuario);
        nameTextView = f1.findViewById(R.id.nameTextView);
        emailTextView = f1.findViewById(R.id.emailTextView);
        espTextView = f1.findViewById(R.id.espTextView);
        lastTextView = f1.findViewById(R.id.lastTextView);


        try {
            Usuario usuario = Common.currentUser;
            String name = usuario.getFirstname();
            String email = usuario.getCorreoG();

            Log.e(TAG, " name :" + name);
            Log.e(TAG, " email :" + email);

            nameTextView.setText(usuario.getFirstname());
            emailTextView.setText(usuario.getCorreoG());
            espTextView.setText(usuario.getEspecialidad());
            lastTextView.setText(usuario.getLastname());

            Glide
                    .with(getActivity())
                    .load(usuario.getImage())
                    .into(photoImageView);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return f1;
    }


}
