package com.cudpast.app.doctor.doctorApp.Business;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Token;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Remote.IFCMService;
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

    private static final String TAG_ERROR = "DoctorHome ";

    private static final int MY_PERMISSION_REQUEST_CODE = 1000;
    private static final int PLAY_SERVICE_RES_REQUEST = 1001;

    private GoogleApiClient mGoogleApiCliente;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    private DatabaseReference drivers, onlineRef, currentUserRef;
    private GeoFire geoFire;
    private Marker mCurrent;

    private MaterialAnimatedSwitch location_switch;// ON or OFF
    public IGoogleAPI mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //<--
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //variables
        location_switch = findViewById(R.id.location_switch);
        //El doctor se pone online o offline ..
        onlineRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(".info/connected");

        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserRef.onDisconnect().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //al estar en Online , se crea la tabla Drivers
        currentUserRef = FirebaseDatabase
                .getInstance()
                .getReference(Common.tb_Business_Doctor)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Inicio de la vista
        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isOnline) {
                if (isOnline) {

                    FirebaseDatabase.getInstance().goOnline();
                    startLocationUpdate();
                    displayLocation();// crear un marker : mCurrent
                    Toast.makeText(mapFragment.getContext(), "Estas Online", Toast.LENGTH_SHORT).show();


                } else {

                    FirebaseDatabase.getInstance().goOffline();
                    stopLocationUpdate();
                    if (mCurrent != null) {
                        mCurrent.remove();// eliminar un marker : mCurrent
                        mMap.clear();// Limpiar mapa
                        Toast.makeText(mapFragment.getContext(), "Estas Offline", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
        //
        drivers = FirebaseDatabase.getInstance().getReference(Common.tb_Business_Doctor);
        Log.e(TAG_ERROR, " drivers");
        geoFire = new GeoFire(drivers);
        Log.e(TAG_ERROR, " geoFire ");
        setUpLocation();//displayLocation();
        Log.e(TAG_ERROR, " setUpLocation() ");
        mService = Common.getGoogleAPI();
        Log.e(TAG_ERROR, " mService ");
        updateFirebaseToken();
        Log.e(TAG_ERROR, "  updateFirebaseToken() ");


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
            // Handle the camera action
        } else if (id == R.id.nav_b) {

        } else if (id == R.id.nav_c) {

        } else if (id == R.id.nav_d) {

        } else if (id == R.id.nav_e) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateFirebaseToken() {
        Log.e(TAG_ERROR, " updateFirebaseToken() ");
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tbl);
        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
        Log.e(TAG_ERROR, "FirebaseAuth.getInstance().getCurrentUser().getUid() -------->" + FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        Log.e("*setUpLocation()", "432");
    }

    private void builGoogleApiClient() {
        mGoogleApiCliente = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiCliente.connect();
        Log.e("*builGoogleApiClient()", " 442" + mGoogleApiCliente);
    }

    private void createLocationRequest() {
        Log.e("createLocationRequest()", "Linea : 444");
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
        Log.e("* checkPlayService()", "Linea : 503");
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
                            Log.e("onRequ", "displayLocation()");
                        }
                    }
                }

        }
    }

    private void stopLocationUpdate() {
        //-->Permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //<--
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiCliente, this);
        Log.e("stopLocationUpdate()", " location_switch : OFF");
    }

    private void displayLocation() {
        //-->Permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //<--
        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCliente);//Obtener GPS del movil
        Common.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCliente);//Obtener GPS del movil
        Log.e(TAG_ERROR, "displayLocation() : Common.mLastLocation --> " + Common.mLastLocation);


        if (Common.mLastLocation != null) {
            if (location_switch.isChecked()) {

                final double latitude = Common.mLastLocation.getLatitude();
                final double longitud = Common.mLastLocation.getLongitude();

                //update to Firebase latitud y longitud de cada usuario
                String geoFireGetUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Log.e(TAG_ERROR, "displayLocation() : geoFireGetUID --> " + geoFireGetUID);
                geoFire.setLocation(geoFireGetUID, new GeoLocation(latitude, longitud), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (mCurrent != null) {
                            mCurrent.remove();
                        }
                        //crear
                        MarkerOptions m1 = new MarkerOptions()
                                .position(new LatLng(latitude, longitud))
                                .icon(BitmapDoctorApp(DoctorHome.this, R.drawable.ic_doctorapp))
                                .title("Usted");

                        mCurrent = mMap.addMarker(m1); // <--Dibujar al doctor en el mapa
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitud), 16.0f));
                    }
                });
            }
        } else {
            Log.d("displayLocation()", "ERROR: Cannot get your location");
        }

        Log.e(TAG_ERROR, "displayLocation() : fin ");
    }

    //todo:revisar
    private void startLocationUpdate() {
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            Common.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCliente);

        } catch (Exception e) {
            Log.e(TAG_ERROR, "startLocationUpdate() : ERROR " + e.getMessage());
            Toast.makeText(this, "ERORR AL ACTIVAR PERMISOSS", Toast.LENGTH_SHORT).show();
        }

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
        Log.e(TAG_ERROR, "Can't find style. Error: ");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //--> Agregar estido de mapa
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.e(TAG_ERROR, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG_ERROR, "Can't find style. Error: ", e);
        }
        //<--

        mMap = googleMap;
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    //metodos auxiliar para imagenes .svg
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
        Log.e(TAG_ERROR, "Linea : 379");
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
}
