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

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Soporte.Token;
import com.cudpast.app.doctor.doctorApp.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class Fragment_2 extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = Fragment_2.class.getSimpleName();

    GoogleMap mMap;
    SupportMapFragment mapFragment;

    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 9000;
    private static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;

    public GoogleApiClient googleApiClient;
    public LocationRequest locationRequest;

    public DatabaseReference refDB_available_doctor, refDB_connect, currentUserRef;
    private GeoFire geoFire;
    private Marker marketDoctorCurrent;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    public Fragment_2() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_2, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapfragment2);
        mapFragment.getMapAsync(this);
        builGoogleApiClient();
        createLocationRequest();
        //Obtener Todas la ubicaciones de los Doctores del TB_Available_Doctor
        //Obtener el UID del doctor
        //Obtener ubicación del doctor
        //On or Off : escuchar el switch
        refDB_available_doctor = FirebaseDatabase.getInstance().getReference(Common.TB_AVAILABLE_DOCTOR);
        currentUserRef = refDB_available_doctor.child(Common.currentUserDoctor.getUid());
        //cuando se pone offline el doctor desaparece en realtime database
        refDB_connect = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        refDB_connect
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //  currentUserRef.onDisconnect().removeValue();
                        Log.e(TAG, "refDB_available_doctor : " + refDB_available_doctor);
                        Log.e(TAG, "currentUserRef : " + currentUserRef);
                        Log.e(TAG, "refDB_connect : " + refDB_connect);

                        currentUserRef
                                .onDisconnect()
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e(TAG, "refDB_connect : onSuccess : ");
                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled " + databaseError);
                    }
                });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        Common.location_switch = rootView.findViewById(R.id.location_switch);
        updateFirebaseToken();
        geoFire = new GeoFire(refDB_available_doctor);
        setUpLocation();

        Common.location_switch
                .setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(boolean isOnline) {
                        if (isOnline) {
                            FirebaseDatabase.getInstance().goOnline();
                            startLocationUpdate();
                            displayLocation();
                        } else {
                            if (marketDoctorCurrent != null) {
                                FirebaseDatabase.getInstance().goOffline();
                                stopLocationUpdate();
                                marketDoctorCurrent.remove();
                                mMap.clear();
                            }
                        }
                    }
                });
        return rootView;
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop() ");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy()");
        super.onDestroy();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    if (checkPlayService()) {
                        builGoogleApiClient();
                        createLocationRequest();
                        if (Common.location_switch.isChecked()) {
                            displayLocation();
                            Log.e(TAG, "displayLocation()" + "onRequestPermissionsResult");
                        }
                    }
                }
            }
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
        Log.e(TAG, "                          setUpLocation()                      ");
        if (ContextCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat
                        .checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Solicitar permiso
            solicitarPermisoCoarseFine();
            Log.e(TAG, "=================================================================");
        } else {
            // Si tiene los permisos - verficiar el  checkPlayService
            verificarServicio();
            Log.e(TAG, "=================================================================");
        }

    }

    private void solicitarPermisoCoarseFine() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSION_REQUEST_CODE);
    }

    private void verificarServicio() {
        if (checkPlayService()) {
            builGoogleApiClient();
            createLocationRequest();
            //solo ocurre si esta activado
            if (Common.location_switch.isChecked()) {
                Log.e(TAG, " Off location ");
                displayLocation();
            }
        } else {
            Log.e(TAG, "error : verificarServicio()");
        }
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

    private void displayLocation() {

        try {
            Log.e(TAG, "=================================================================");
            Log.e(TAG, "                          displayLocation()                      ");

            if (ContextCompat
                    .checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat
                            .checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

                //.Obtener GPS del movil
                fusedLocationClient
                        .getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Common.mLastLocation = location;
                                    Log.e(TAG, " Common.mLastLocation = " + Common.mLastLocation );
                                    if (Common.mLastLocation != null && Common.location_switch.isChecked()) {
                                        //
                                        final ProgressDialog mDialog = new ProgressDialog(getActivity());
                                        mDialog.setMessage("Actualizando su Ubicación...");
                                        mDialog.show();
                                        //
                                        String firebaseUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();//la llave
                                        final double latitude = Common.mLastLocation.getLatitude();
                                        final double longitud = Common.mLastLocation.getLongitude();

                                        Log.e(TAG, " firebaseUserUID =  " + firebaseUserUID);
                                        Log.e(TAG, " Common.mLastLocation.getLatitude()  = " + latitude);
                                        Log.e(TAG, " Common.mLastLocation.getLongitude() =  " + longitud);

                                        geoFire.setLocation(firebaseUserUID, new GeoLocation(latitude, longitud), new GeoFire.CompletionListener() {
                                            @Override
                                            public void onComplete(String key, DatabaseError error) {

                                                if (marketDoctorCurrent != null) {
                                                    marketDoctorCurrent.remove();
                                                }

                                                try {
                                                    MarkerOptions m1 = new MarkerOptions()
                                                            .position(new LatLng(latitude, longitud))
                                                            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_doctorapp))
                                                            .title("Usted");
                                                    //Dibujar al doctor en el mapa
                                                    marketDoctorCurrent = mMap.addMarker(m1);
                                                    LatLng doctorLL = new LatLng(latitude, longitud);
                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(doctorLL, 16.0f));
                                                    mDialog.dismiss();
                                                }catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                    } else {
                                        Log.e(TAG, "ERROR: Cannot get your location");
                                        Log.e(TAG, "ERROR: no es checkeado");
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "addOnFailureListener :" + e.getMessage());
                            }
                        });

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_CODE);
            }
            Log.e(TAG, "=================================================================");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void startLocationUpdate() {
        try {
            Log.e(TAG, "startLocationUpdate()");
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    private void stopLocationUpdate() {
        try {
            Log.e(TAG, " stopLocationUpdate() " + " location_switch : OFF");
            if (ContextCompat
                    .checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat
                            .checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
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


}
