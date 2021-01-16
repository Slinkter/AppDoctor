package com.cudpast.app.doctor.doctorApp.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.PacienteProfile;
import com.cudpast.app.doctor.doctorApp.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;


public class Fragment_3 extends Fragment {

    public static final String TAG = Fragment_3.class.getSimpleName();
    private View viewFragment;
    private String uid_doctor;
    private RecyclerView rv_historyListDoctor;
    private DatabaseReference refDB_AppDoctor_history;
    private FirebaseAuth auth;
    private FirebaseRecyclerAdapter<PacienteProfile, myPacienteVH> firebase_recycler_adapter;
    private FirebaseRecyclerOptions firebase_recycler_options;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewFragment = inflater.inflate(R.layout.fragment_3, container, false);

        auth = FirebaseAuth.getInstance();
        uid_doctor = auth.getCurrentUser().getUid();

        refDB_AppDoctor_history = FirebaseDatabase.getInstance().getReference(Common.AppDoctor_history).child(uid_doctor);
        refDB_AppDoctor_history.keepSynced(true);
        refDB_AppDoctor_history.limitToLast(10);
       refDB_AppDoctor_history.orderByKey();

        rv_historyListDoctor = viewFragment.findViewById(R.id.myrecycleviewHistory);
        rv_historyListDoctor.setHasFixedSize(true);
        rv_historyListDoctor.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.e(TAG, "====================================");
        Log.e(TAG, "uid_doctor = " + uid_doctor);
        Log.e(TAG, "====================================");
        Log.e(TAG, "refDB_AppDoctor_history = " + refDB_AppDoctor_history.getDatabase());
        Log.e(TAG, "====================================");
        Log.e(TAG, "rv_historyListDoctor = " + rv_historyListDoctor);
        Log.e(TAG, "====================================");
        Log.e(TAG, "refDB_AppDoctor_history = " + refDB_AppDoctor_history);

        return viewFragment;
    }


    @Override
    public void onStart() {
        super.onStart();
        //Options


        firebase_recycler_options = new FirebaseRecyclerOptions
                .Builder<PacienteProfile>()
                .setQuery(refDB_AppDoctor_history, PacienteProfile.class)
                .build();
        //Adapter
        firebase_recycler_adapter = new FirebaseRecyclerAdapter<PacienteProfile, myPacienteVH>(firebase_recycler_options) {

            @Override
            protected void onBindViewHolder(@NonNull myPacienteVH pacienteVH, int position, @NonNull PacienteProfile pacienteModel) {
                //imprime info de la atención del pacienteModel
                Log.e(TAG, " onBindViewHolder :  position = " + position);
                pacienteVH.setDateVisit(pacienteModel.getDateborn());
                pacienteVH.setFirstName(pacienteModel.getFirstname());
                pacienteVH.setLastName(pacienteModel.getLastname());
                pacienteVH.setAddress(pacienteModel.getAddress());
            }

            @NonNull
            @Override
            public myPacienteVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.layout_paciente_info, parent, false);
                myPacienteVH myPacienteVH = new myPacienteVH(view);
                return myPacienteVH;
            }
        };

        Log.e(TAG, "firebase_recycler_options : " + firebase_recycler_options);
        Log.e(TAG, "firebase_recycler_adapter : " + firebase_recycler_adapter);
        Log.e(TAG, "rv_historyListDoctor : " + rv_historyListDoctor);


        rv_historyListDoctor.setAdapter(firebase_recycler_adapter);
        firebase_recycler_adapter.startListening();
    }


    public static class myPacienteVH extends RecyclerView.ViewHolder {

        View mView;
        TextView post_date, post_firstName, post_lastName, post_phone;


        public myPacienteVH(@NonNull final View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDateVisit(String dateVisit) {
            post_date = mView.findViewById(R.id.datevisit);
            post_date.setText(dateVisit);
        }

        public void setFirstName(String firstName) {
            post_firstName = mView.findViewById(R.id.firstname);
            post_firstName.setText(firstName);
        }

        public void setLastName(String lastName) {
            post_lastName = mView.findViewById(R.id.lastname);
            post_lastName.setText(lastName);
        }

        public void setAddress(String phone) {
            post_phone = mView.findViewById(R.id.phone);
            post_phone.setText(phone);

        }


    }
}
