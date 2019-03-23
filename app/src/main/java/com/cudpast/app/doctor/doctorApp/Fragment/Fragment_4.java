package com.cudpast.app.doctor.doctorApp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cudpast.app.doctor.doctorApp.Activities.Extra.UpdatePhotoDoctor;
import com.cudpast.app.doctor.doctorApp.Activities.Extra.UpdateProfileDoctor;
import com.cudpast.app.doctor.doctorApp.Activities.Extra.UpdatePwdDoctor;
import com.cudpast.app.doctor.doctorApp.R;


public class Fragment_4 extends Fragment {

    public Fragment_4() {

    }

    Button btn_update_info_doctor;
    Button btn_update_photo_doctor;
    Button btn_update_pwd_doctor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


        View rootView;
        rootView = inflater.inflate(R.layout.fragment_4, container, false);

        btn_update_info_doctor = (Button)  rootView.findViewById(R.id.btn_update_info_doctor);
        btn_update_photo_doctor = (Button)  rootView.findViewById(R.id.btn_update_photo_doctor);
        btn_update_pwd_doctor = (Button)  rootView.findViewById(R.id.btn_update_pwd_doctor);

        btn_update_info_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UpdateProfileDoctor.class);
                startActivity(intent);

            }
        });

        btn_update_photo_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UpdatePhotoDoctor.class);
                startActivity(intent);

            }
        });

        btn_update_pwd_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UpdatePwdDoctor.class);
                startActivity(intent);

            }
        });


        return rootView;
    }


}
