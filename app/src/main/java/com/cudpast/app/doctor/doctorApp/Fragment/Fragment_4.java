package com.cudpast.app.doctor.doctorApp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cudpast.app.doctor.doctorApp.Activities.Extra.UpdateProfileDoctorActivity;
import com.cudpast.app.doctor.doctorApp.R;


public class Fragment_4 extends Fragment {

    public Fragment_4() {

    }

    Button btn_update_info_doctor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


        View rootView;
        rootView = inflater.inflate(R.layout.fragment_4, container, false);

        btn_update_info_doctor = (Button)  rootView.findViewById(R.id.btn_update_info_doctor);

        btn_update_info_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UpdateProfileDoctorActivity.class);
                startActivity(intent);

            }
        });
        return rootView;
    }


}
