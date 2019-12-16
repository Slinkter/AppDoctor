package com.cudpast.app.doctor.doctorApp.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private FirebaseRecyclerAdapter<PacienteProfile, myPacienteViewHolder> firebase_adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewFragment = inflater.inflate(R.layout.fragment_3, container, false);

        auth = FirebaseAuth.getInstance();
        uid_doctor = auth.getCurrentUser().getUid();

        refDB_AppDoctor_history = FirebaseDatabase.getInstance().getReference(Common.AppDoctor_history).child(uid_doctor);
        refDB_AppDoctor_history.keepSynced(true);
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
        FirebaseRecyclerOptions myfirebaseRecyclerOptions;
        myfirebaseRecyclerOptions = new FirebaseRecyclerOptions
                .Builder<PacienteProfile>()
                .setQuery(refDB_AppDoctor_history, PacienteProfile.class)
                .build();
        //Adapter

        firebase_adapter = new FirebaseRecyclerAdapter<PacienteProfile, myPacienteViewHolder>(myfirebaseRecyclerOptions) {

            @Override
            protected void onBindViewHolder(@NonNull myPacienteViewHolder holder, int position, @NonNull PacienteProfile model) {
                holder.setDateVisit(model.getDateborn());
                holder.setFirstName(model.getFirstname());
                holder.setLastName(model.getLastname());
                holder.setAddress(model.getAddress());
                //

                Log.e(TAG, "datevisit : " + model.getFirstname());
                Log.e(TAG, "getFirstName : " + model.getFirstname());
                Log.e(TAG, "getLastName : " + model.getLastname());
                Log.e(TAG, "getAddress : " + model.getAddress());
            }

            @NonNull
            @Override
            public myPacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.paciente_layout_info, parent, false);
                myPacienteViewHolder viewHolder = new myPacienteViewHolder(v1);
                return viewHolder;
            }
        };

        Log.e(TAG, "myfirebaseRecyclerOptions : " + myfirebaseRecyclerOptions);
        Log.e(TAG, "firebase_adapter : " + firebase_adapter);
        Log.e(TAG, "rv_historyListDoctor : " + rv_historyListDoctor);


        rv_historyListDoctor.setAdapter(firebase_adapter);
        firebase_adapter.startListening();
    }


    public static class myPacienteViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView post_date, post_firstName, post_lastName, post_phone;


        public myPacienteViewHolder(@NonNull final View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDateVisit(String dateVisit){
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
