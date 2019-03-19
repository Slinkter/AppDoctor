package com.cudpast.app.doctor.doctorApp.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.Token;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Remote.IGoogleAPI;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitchState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class Fragment_2 extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = Fragment_2.class.getSimpleName();

    GoogleMap mMap;
    SupportMapFragment mapFragment;

    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 9000;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    public GoogleApiClient googleApiClient;
    public LocationRequest locationRequest;


    public DatabaseReference db_available_doctor, db_online_offline_user, db_currentUserRef;
    private GeoFire geoFire;
    private Marker marketDoctorCurrent;

    private MaterialAnimatedSwitch location_switch;
    public String current_user_UID;


    //parte012b
    private FusedLocationProviderClient fusedLocationClient;

    boolean esta_online, switch_on;
    int valor1;
    //
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public Fragment_2() {

    }

    //Todo : Guardar en savedinstar la variable location_switch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_2, container, false);

        builGoogleApiClient();
        createLocationRequest();

        if (savedInstanceState == null) {
            Log.e(TAG, "Hola null 1");
        } else {
            valor1 = savedInstanceState.getInt("prueba2", 0);
            Boolean prueba1 = savedInstanceState.getBoolean("prueba1");
            Log.e(TAG, "onCreateView : savedInstanceState : prueba 1 " + prueba1);
            Log.e(TAG, "onCreateView : savedInstanceState : prueba 2 " + valor1);
        }


        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapfragment2);
        mapFragment.getMapAsync(this);
        builGoogleApiClient();
        createLocationRequest();


        //-->
        //Obtener el UID del doctor
        //Obtener Todas la ubicaciones de los Doctores del TB_Available_Doctor  : g y l : 0 y 1
        //Obtener ubicación del doctor
        //On or Off : escucha el switch
        current_user_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db_available_doctor = FirebaseDatabase.getInstance().getReference(Common.TB_AVAILABLE_DOCTOR);
        db_currentUserRef = db_available_doctor.child(current_user_UID);
        db_online_offline_user = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        db_online_offline_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                db_currentUserRef.onDisconnect().removeValue();
                Log.e(TAG, "current_user_UID " + current_user_UID);
                Log.e(TAG, "db_available_doctor " + db_available_doctor);
                Log.e(TAG, "db_currentUserRef " + db_currentUserRef);
                Log.e(TAG, "db_online_offline_user " + db_online_offline_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled " + databaseError);
            }
        });


        //parte012b
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        location_switch = rootView.findViewById(R.id.location_switch);

        setUpLocation();
        updateFirebaseToken();

        geoFire = new GeoFire(db_available_doctor);// g y (l : 0 y 1)

        location_switch
                .setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(boolean isOnline) {
                        if (isOnline) {
                            FirebaseDatabase.getInstance().goOnline();
                            startLocationUpdate();
                            displayLocation();
                            esta_online = true;
                            Toast.makeText(mapFragment.getContext(), "Online", Toast.LENGTH_SHORT).show();
                        } else {
                            if (marketDoctorCurrent != null) {
                                FirebaseDatabase.getInstance().goOffline();
                                stopLocationUpdate();
                                marketDoctorCurrent.remove();
                                mMap.clear();
                                Toast.makeText(mapFragment.getContext(), "Offline", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                });


        return rootView;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("prueba1", esta_online);
        outState.putInt("prueba2", valor1);
        Log.e(TAG, "onSaveInstanceState" + esta_online);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            Log.e(TAG, "Hola null 2 ");
        } else {
            valor1 = savedInstanceState.getInt("prueba2", 0);
            esta_online = savedInstanceState.getBoolean("prueba1", false);
            Log.e(TAG, "onCreateView : savedInstanceState : prueba 1 " + esta_online);
            Log.e(TAG, "onCreateView : savedInstanceState : prueba 2 " + valor1);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState == null) {
            Log.e(TAG, "Hola null 3");
        } else {
            valor1 = savedInstanceState.getInt("prueba2", 0);
            Boolean prueba1 = savedInstanceState.getBoolean("prueba1");
            Log.e(TAG, "onCreateView : savedInstanceState : prueba 1 " + prueba1);
            Log.e(TAG, "onCreateView : savedInstanceState : prueba 2 " + valor1);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    private void updateFirebaseToken() {
        Log.e(TAG, " updateFirebaseToken() ");
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tbl);
        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    private void setUpLocation() {
        Log.e(TAG, "=================================================================");
        Log.e(TAG, "                          setUpLocation()()                      ");

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayService()) {
                builGoogleApiClient();
                createLocationRequest();
                //solo ocurre si esta activado
                if (location_switch.isChecked()) {
                    Log.e(TAG, " Off location ");
                    displayLocation();
                }
            } else {
                Log.e(TAG, "ERROR: checkPlayService()");
            }
        }
        Log.e(TAG, "=================================================================");
    }

    private boolean checkPlayService() {
        Log.e(TAG, "checkPlayService() ");
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(getActivity());
        Log.e(TAG, "resultCode() " + resultCode);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e(TAG, "resultCode() :  NOT SUCCESS ");
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(getActivity(), resultCode, PLAY_SERVICE_RES_REQUEST).show();
            }
            return false;
        } else {
            Log.e(TAG, "resultCode() : SUCCESS ");
        }
        return true;
    }

    private void builGoogleApiClient() {
        Log.e(TAG, "builGoogleApiClient()");
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void createLocationRequest() {
        Log.e(TAG, "createLocationRequest()");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (checkPlayService()) {
//                        builGoogleApiClient();
//                        createLocationRequest();
//                        if (location_switch.isChecked()) {
//                            displayLocation();
//                            Log.e(TAG, "displayLocation()" + "onRequestPermissionsResult");
//                        }
//                    }
//                }
//
//        }


        //
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
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

    }


    private void displayLocation() {
        Log.e(TAG, "=================================================================");
        Log.e(TAG, "                          displayLocation()                      ");
        //.Permisos

        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


        //.Obtener GPS del movil

        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Common.mLastLocation = location;
                            Log.e(TAG, "fusedLocationClient : Common.mLastLocation.getLatitude() " + Common.mLastLocation.getLatitude());
                            Log.e(TAG, "fusedLocationClient : Common.mLastLocation.getLongitude()" + Common.mLastLocation.getLongitude());

                        }
                    }
                });

        Log.e(TAG, " Common.mLastLocation : " + Common.mLastLocation);
        if (Common.mLastLocation != null) {
            if (location_switch.isChecked()) {
                //
                final ProgressDialog mDialog = new ProgressDialog(getActivity());
                mDialog.setMessage("Actualizando su Ubicación...");
                mDialog.show();
                //
                final double latitude = Common.mLastLocation.getLatitude();
                final double longitud = Common.mLastLocation.getLongitude();
                String firebaseUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();//la llave

                Log.e(TAG, " firebaseUserUID :  " + firebaseUserUID);
                Log.e(TAG, " Common.mLastLocation.getLatitude()  :  " + latitude);
                Log.e(TAG, " Common.mLastLocation.getLongitude() :  " + longitud);

                geoFire.setLocation(firebaseUserUID, new GeoLocation(latitude, longitud), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                        if (marketDoctorCurrent != null) {
                            marketDoctorCurrent.remove();
                        }
                        MarkerOptions m1 = new MarkerOptions()
                                .position(new LatLng(latitude, longitud))
                                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_doctorapp))
                                .title("Usted");
                        //Dibujar al doctor en el mapa
                        marketDoctorCurrent = mMap.addMarker(m1);
                        LatLng doctorLL = new LatLng(latitude, longitud);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(doctorLL, 16.0f));
                        mDialog.dismiss();
                    }
                });

            } else {

                Log.e(TAG, "ERROR: no es checkeado");
            }
        } else {
            Log.e(TAG, "ERROR: Cannot get your location");
        }
        Log.e(TAG, "=================================================================");
    }

    private void stopLocationUpdate() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        Log.e(TAG, " stopLocationUpdate() " + " location_switch : OFF");
    }


    private void startLocationUpdate() {
        Log.e(TAG, "startLocationUpdate()");
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();
        //   startLocationUpdate();


    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Common.mLastLocation = location;
        displayLocation();
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth() + 0, vectorDrawable.getIntrinsicHeight() + 0);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart " + valor1 + " : " + esta_online);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop " + valor1 + " : " + esta_online);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause " + valor1 + " : " + esta_online);
        if (location_switch.isActivated() == true) {
            switch_on = true;
        } else {
            switch_on = false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume " + valor1 + " : " + esta_online);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_CODE);
        }
    }




}
