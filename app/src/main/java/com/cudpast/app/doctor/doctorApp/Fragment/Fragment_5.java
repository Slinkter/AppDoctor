package com.cudpast.app.doctor.doctorApp.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cudpast.app.doctor.doctorApp.R;


public class Fragment_5 extends Fragment {


    public Fragment_5() {
        // Required empty public constructor
    }

    Button salirapp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView;
        rootView = inflater.inflate(R.layout.fragment_5,container,false);


        salirapp = (Button)  rootView.findViewById(R.id.idsalirapp);

        salirapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.exit(0);



                //  Intent intent = new Intent(Intent.ACTION_MAIN);
                //intent.addCategory(Intent.CATEGORY_HOME);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(intent);

            }
        });
        return rootView;
    }


}
