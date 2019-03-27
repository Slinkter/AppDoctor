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
import com.cudpast.app.doctor.doctorApp.Model.UserPaciente;
import com.cudpast.app.doctor.doctorApp.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;


public class Fragment_3 extends Fragment {

    public static final String TAG = Fragment_3.class.getSimpleName();



    private View viewFragment;
    private String userUID;
    private RecyclerView mBlogList;
    private DatabaseReference AppDoctor_history;
    private FirebaseAuth auth;

    private FirebaseRecyclerAdapter<UserPaciente, pacienteViewHolder> adapter;

    public Fragment_3() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewFragment = inflater.inflate(R.layout.fragment_3, container, false);

        auth = FirebaseAuth.getInstance();
        userUID = auth.getCurrentUser().getUid();


        AppDoctor_history = FirebaseDatabase.getInstance().getReference(Common.AppDoctor_history).child(userUID);
        AppDoctor_history.keepSynced(true);
        AppDoctor_history.orderByKey();

        mBlogList = viewFragment.findViewById(R.id.myrecycleviewHistory);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.e(TAG,"userUID : " +userUID);
        Log.e(TAG,"AppDoctor_history : " +AppDoctor_history);
        Log.e(TAG,"mBlogList : " +mBlogList);
        Log.e(TAG,"AppDoctor_history : " +AppDoctor_history);

        return viewFragment;
    }


    @Override
    public void onStart() {
        super.onStart();
        //Options
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<UserPaciente>()
                .setQuery(AppDoctor_history, UserPaciente.class)
                .build();




        //Adapter

        adapter = new FirebaseRecyclerAdapter<UserPaciente, pacienteViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull pacienteViewHolder holder, int position, @NonNull UserPaciente model) {

                Log.e(TAG,"holder : " +holder);
                Log.e(TAG,"getNombre : " +model.getNombre());
                Log.e(TAG,"getApellido : " +model.getApellido());
                holder.setFirstName(model.getNombre());
                holder.setLastName(model.getApellido());
                holder.setPhone(model.getDirecion());
            }

            @NonNull
            @Override
            public pacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.paciente_layout_info, parent, false);
                pacienteViewHolder viewHolder = new pacienteViewHolder(v1);

                return viewHolder;
            }
        };

        Log.e(TAG,"options : " +options);
        Log.e(TAG,"adapter : " +adapter);
        Log.e(TAG,"mBlogList : " +mBlogList);


        mBlogList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class pacienteViewHolder extends RecyclerView.ViewHolder {


        TextView post_firstName, post_lastName, post_phone;
        View mView;

        public pacienteViewHolder(@NonNull final View itemView) {
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
