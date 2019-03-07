package com.cudpast.app.doctor.doctorApp.Business;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.app.doctor.doctorApp.Activities.LoginActivity;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Fragment.Fragment_1;
import com.cudpast.app.doctor.doctorApp.Fragment.Fragment_2;
import com.cudpast.app.doctor.doctorApp.Fragment.Fragment_3;
import com.cudpast.app.doctor.doctorApp.Fragment.Fragment_4;
import com.cudpast.app.doctor.doctorApp.Fragment.Fragment_5;
import com.cudpast.app.doctor.doctorApp.Model.Token;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Remote.IGoogleAPI;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DoctorHome extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private static final String TAG = DoctorHome.class.getSimpleName();

    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private GoogleApiClient mGoogleApiCliente;
    private LocationRequest mLocationRequest;
    public IGoogleAPI mService;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    public DatabaseReference FirebaseDB_drivers, FirebaseDB_onlineRef, FirebaseDB_currentUserRef;
    private GeoFire geoFire;
    private Marker marketDoctorCurrent;

    private MaterialAnimatedSwitch location_switch;// ON or OFF


    //Header Menu
    ImageView imageViewDoctor;
    TextView nameDoctor;
    TextView emailDoctor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        //
        imageViewDoctor = (ImageView) headerView.findViewById(R.id.imageViewDoctor);
        nameDoctor = (TextView)headerView.findViewById(R.id.nameDoctor);
        emailDoctor = (TextView)headerView.findViewById(R.id.emailDoctor);

        //<--
//        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment2);
//        mapFragment.getMapAsync(this);
        //variables
//        location_switch = findViewById(R.id.location_switch);
//        //El doctor se pone online o offline ..
//        //al estar en Online , se crea la tabla Drivers
//        String Userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        FirebaseDB_currentUserRef = FirebaseDatabase.getInstance().getReference(Common.TB_AVAILABLE_DOCTOR).child(Userid);
//
//
//        FirebaseDB_onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
//
//        FirebaseDB_onlineRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                FirebaseDB_currentUserRef.onDisconnect().removeValue();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });

//        //Online-Offline Doctor
//        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(boolean isOnline) {
//                if (isOnline) {
//                    FirebaseDatabase.getInstance().goOnline();
//                    startLocationUpdate();
//                    displayLocation();// crear un marker : marketDoctorCurrent
//                    Toast.makeText(mapFragment.getContext(), "Estas Online", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        FirebaseDatabase.getInstance().goOffline();
//                        stopLocationUpdate();
//                        marketDoctorCurrent.remove();
//                        mMap.clear();
//                        Toast.makeText(mapFragment.getContext(), "Estas Offline", Toast.LENGTH_SHORT).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        //
//        FirebaseDB_drivers = FirebaseDatabase.getInstance().getReference(Common.TB_AVAILABLE_DOCTOR);
//        geoFire = new GeoFire(FirebaseDB_drivers);
//        setUpLocation();
//        mService = Common.getGoogleAPI();
//        updateFirebaseToken();
        setFragment(1);


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

        if (id == R.id.nav_a) {
            setFragment(1);
        } else if (id == R.id.nav_b) {
            setFragment(2);
        } else if (id == R.id.nav_c) {
            setFragment(3);
        } else if (id == R.id.nav_d) {
            setFragment(4);
        } else if (id == R.id.nav_e) {
            setFragment(5);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateFirebaseToken() {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tbl);
        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);

        Log.e(TAG, " updateFirebaseToken() ");
        Log.e(TAG, "FirebaseAuth.getInstance().getCurrentUser().getUid() -------->" + FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private void setUpLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayService()) {
                builGoogleApiClient();
                createLocationRequest();
                if (location_switch.isChecked()) {
                    displayLocation();
                }
            }
        }
        Log.e(TAG, "*setUpLocation()");
    }

    private void builGoogleApiClient() {
        mGoogleApiCliente = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiCliente.connect();
        Log.e(TAG, " *builGoogleApiClient()" + mGoogleApiCliente);
    }

    private void createLocationRequest() {
        Log.e(TAG, "*createLocationRequest()");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayService() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "this device is support ", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        Log.e(TAG, "*checkPlayService()");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayService()) {
                        builGoogleApiClient();
                        createLocationRequest();
                        if (location_switch.isChecked()) {
                            displayLocation();
                            Log.e(TAG, "displayLocation()" + "onRequestPermissionsResult");
                        }
                    }
                }

        }
    }

    private void stopLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiCliente, this);
        Log.e(TAG, " stopLocationUpdate() " + " location_switch : OFF");
    }

    private void displayLocation() {
        Log.e(TAG, "=================================================================");
        Log.e(TAG, "                          displayLocation()                      ");
        //.Permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //.Obtener GPS del movil
        Common.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCliente);
        Log.e(TAG, "displayLocation() : Common.mLastLocation --> " + Common.mLastLocation);
        //.Update to firebaseUserUID latitud y longitud de cada usuario

        if (Common.mLastLocation != null) {
            if (location_switch.isChecked()) {

                final double latitude = Common.mLastLocation.getLatitude();
                final double longitud = Common.mLastLocation.getLongitude();
                String firebaseUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();//la llave

                Log.e(TAG, " firebaseUserUID : --> " + firebaseUserUID);
                Log.e(TAG, " Common.mLastLocation.getLatitude() : --> " + latitude);
                Log.e(TAG, " Common.mLastLocation.getLongitude(): --> " + longitud);

                geoFire.setLocation(firebaseUserUID, new GeoLocation(latitude, longitud), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                        if (marketDoctorCurrent != null) {
                            marketDoctorCurrent.remove();
                        }
                        MarkerOptions m1 = new MarkerOptions()
                                .position(new LatLng(latitude, longitud))
                                .icon(BitmapDoctorApp(DoctorHome.this, R.drawable.ic_doctorapp))
                                .title("Usted");
                        //Dibujar al doctor en el mapa
                        marketDoctorCurrent = mMap.addMarker(m1);
                        LatLng doctorLL = new LatLng(latitude, longitud);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(doctorLL, 16.0f));
                    }
                });
            } else {
                Log.e(TAG, "displayLocation()" + "ERROR: no es checkeado");
            }
        } else {
            Log.e(TAG, "displayLocation()" + "ERROR: Cannot get your location");
        }
        Log.e(TAG, "=================================================================");
    }

    //todo:revisar
    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiCliente, mLocationRequest, this);
        Log.e(TAG, "startLocationUpdate() --> Permiso");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiCliente.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Common.mLastLocation = location;
        displayLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


    }

    //.metodos auxiliar para imagenes .svg
    private BitmapDescriptor BitmapDoctorApp(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private List<LatLng> decodePoly(String encoded) {
        Log.e(TAG, "Linea : 379");
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Usuario usuario = Common.currentUser;
        if (usuario != null) {
           Log.e(TAG,"ok");
           cargarDataDoctor();
        } else {
            goToLoginActivity();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    //.LOGIN_ACTIVITY
    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    //.
    private void cargarDataDoctor(){
        Usuario usuario = Common.currentUser;

        String name = usuario.getFirstname();
        String email = usuario.getCorreoG();

        Log.e(TAG," name :" + name);
        Log.e(TAG," email :" + email);
        try {
            nameDoctor.setText(name);
            emailDoctor.setText(email);
            Picasso
                    .with(this)
                    .load(Common.currentUser.getImage())
                    .resize(80,80)
                    .placeholder(R.drawable.ic_photo_doctor)
                    .error(R.drawable.ic_photo_doctor)
                    .into(imageViewDoctor);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

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
            case 5:
                //Salir
                Fragment_5 fragment5 = new Fragment_5();
                transaction.replace(R.id.fragment, fragment5);
                transaction.commit();
                break;


        }




    }

}
