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
    private RecyclerView mBlogList;
    private DatabaseReference AppDoctor_history;
    private FirebaseAuth auth;
    private FirebaseRecyclerAdapter<PacienteProfile, myPacienteViewHolder> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewFragment = inflater.inflate(R.layout.fragment_3, container, false);

        auth = FirebaseAuth.getInstance();
        uid_doctor = auth.getCurrentUser().getUid();

        AppDoctor_history = FirebaseDatabase.getInstance().getReference(Common.AppDoctor_history).child(uid_doctor);
        AppDoctor_history.keepSynced(true);
        AppDoctor_history.orderByKey();

        mBlogList = viewFragment.findViewById(R.id.myrecycleviewHistory);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.e(TAG, "uid_doctor : " + uid_doctor);
        Log.e(TAG, "AppDoctor_history : " + AppDoctor_history);
        Log.e(TAG, "mBlogList : " + mBlogList);
        Log.e(TAG, "AppDoctor_history : " + AppDoctor_history);

        return viewFragment;
    }


    @Override
    public void onStart() {
        super.onStart();
        //Options
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions
                .Builder<PacienteProfile>()
                .setQuery(AppDoctor_history, PacienteProfile.class)
                .build();

        //Adapter

        adapter = new FirebaseRecyclerAdapter<PacienteProfile, myPacienteViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull myPacienteViewHolder holder, int position, @NonNull PacienteProfile model) {
                holder.setFirstName(model.getFirstname());
                holder.setLastName(model.getLastname());
                holder.setPhone(model.getAddress());
                //
                Log.e(TAG, "holder : " + holder);
                Log.e(TAG, "getNombre : " + model.getFirstname());
                Log.e(TAG, "getApellido : " + model.getLastname());
            }

            @NonNull
            @Override
            public myPacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.paciente_layout_info, parent, false);
                myPacienteViewHolder viewHolder = new myPacienteViewHolder(v1);
                return viewHolder;
            }
        };

        Log.e(TAG, "options : " + options);
        Log.e(TAG, "adapter : " + adapter);
        Log.e(TAG, "mBlogList : " + mBlogList);


        mBlogList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class myPacienteViewHolder extends RecyclerView.ViewHolder {


        TextView post_firstName, post_lastName, post_phone;
        View mView;

        public myPacienteViewHolder(@NonNull final View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFirstName(String firstName) {
            post_firstName = mView.findViewById(R.id.firstname);
            post_firstName.setText(firstName);
        }

        public void setLastName(String lastName) {
            post_lastName = mView.findViewById(R.id.lastname);
            post_lastName.setText(lastName);
        }

        public void setPhone(String phone) {
            post_phone = mView.findViewById(R.id.phone);
            post_phone.setText(phone);

        }


    }
}
