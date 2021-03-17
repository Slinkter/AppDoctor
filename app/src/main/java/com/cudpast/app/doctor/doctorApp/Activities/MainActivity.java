package com.cudpast.app.doctor.doctorApp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Fragment.Fragment_1;
import com.cudpast.app.doctor.doctorApp.Fragment.Fragment_2;
import com.cudpast.app.doctor.doctorApp.Fragment.Fragment_3;
import com.cudpast.app.doctor.doctorApp.Fragment.Fragment_4;
import com.cudpast.app.doctor.doctorApp.Model.DoctorProfile;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //Header Menu
    ImageView imageViewDoctor;
    TextView nameDoctor;
    TextView especialidadDoctor;
    //
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        //
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        //
        imageViewDoctor = (ImageView) headerView.findViewById(R.id.imageViewDoctor);
        nameDoctor = (TextView) headerView.findViewById(R.id.nameDoctor);
        especialidadDoctor = (TextView) headerView.findViewById(R.id.especialidadDoctor);

        setFragment(1);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // AlertNoGps();
             displayLocationNull();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displayLocationNull() {

        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.alert_location_null, null);
            builder.setView(view);
            builder.setCancelable(false);
            view.setKeepScreenOn(true);
            final android.app.AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            Button btn_gps_active = view.findViewById(R.id.btn_gps_active);
            Button btn_gps_cancele = view.findViewById(R.id.btn_gps_cancele);

            btn_gps_active.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialog.dismiss();
                }
            });

            btn_gps_cancele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doctor_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //
        if (id == R.id.nav_a) {
            setFragment(1);
        } else if (id == R.id.nav_b) {
            setFragment(2);
        } else if (id == R.id.nav_c) {
            setFragment(3);
        } else if (id == R.id.nav_d) {
            setFragment(4);
        } else if (id == R.id.nav_e) {
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }
        //
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        DoctorProfile doctorProfile = Common.currentUserDoctor;
        if (doctorProfile != null) {
            loadInfoDoctorHeader();
        } else {
            goToLoginActivity();
        }
    }

    //.
    private void loadInfoDoctorHeader() {
        Log.e(TAG, "usuario esta logeado");
        try {
            DoctorProfile doctorProfile = Common.currentUserDoctor;

            String name = doctorProfile.getFirstname() + " " + doctorProfile.getLastname();
            String especialidad = doctorProfile.getEspecialidad();
            String urlImg = doctorProfile.getImagePhoto();

            nameDoctor.setText(name);
            especialidadDoctor.setText(especialidad);
            Glide
                    .with(this)
                    .load(Common.currentUserDoctor.getImagePhoto())
                    .placeholder(R.drawable.ic_doctorapp)
                    .error(R.drawable.ic_doctorapp)
                    .into(imageViewDoctor);

            Log.e(TAG, " name = " + name);
            Log.e(TAG, " especialidad = " + especialidad);
            Log.e(TAG, " urlImg = " + urlImg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //.
    private void goToLoginActivity() {
        Log.e(TAG, "usuario no logeado");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    //.
    public void setFragment(int pos) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (pos) {
            case 1:
                //Inicio
                Fragment_1 fragment1 = new Fragment_1();
                transaction.replace(R.id.fragment, fragment1);
                transaction.commit();
                break;
            case 2:
                //Servicio
                Fragment_2 fragment2 = new Fragment_2();
                transaction.replace(R.id.fragment, fragment2);
                transaction.commit();
                break;
            case 3:
                //hitorial
                Fragment_3 fragment3 = new Fragment_3();
                transaction.replace(R.id.fragment, fragment3);
                transaction.commit();
                break;
            case 4:
                //configuracion
                Fragment_4 fragment4 = new Fragment_4();
                transaction.replace(R.id.fragment, fragment4);
                transaction.commit();
                break;
        }


    }

}
