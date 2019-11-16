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
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 9000;
    private static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    public GoogleApiClient googleApiClient;
    public LocationRequest locationRequest;
    public DatabaseReference ref_online_all_doctor, refDB_connect, ref_online_doctor;
    public GeoFire geoFireLocationDoctor;
    public Marker marketDoctorCurrent;
    public FusedLocationProviderClient fusedLocationClient;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public boolean mLocationPermissionGranted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_2, container, false);

        //Obtener Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        Common.location_switch = view.findViewById(R.id.location_switch);
        //Obtener fragment de Google Maps
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapfragment2);
        mapFragment.getMapAsync(this);

        //Obtener Todas la ubicaciones de los Doctores del TB_Available_Doctor
        ref_online_all_doctor = FirebaseDatabase.getInstance().getReference(Common.TB_AVAILABLE_DOCTOR);
        //Obtener el UID del doctor online
        // ref_online_doctor = ref_online_all_doctor.child(Common.currentUserDoctor.getUid());
        //Obtener ubicación del doctor
        geoFireLocationDoctor = new GeoFire(ref_online_all_doctor);

        Common.location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isOnline) {
                if (isOnline) {
                    FirebaseDatabase.getInstance().goOnline();
                    startLocationUpdate();
                    displayLocationOnline();
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

        refDB_connect = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        refDB_connect.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (connected) {
                    Log.e(TAG, "connected");
                    Toast.makeText(view.getContext(), "Estas Online", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Not connected");
                    Toast.makeText(view.getContext(), "Estas Offline", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, " onCancelled : Listener was cancelled" + databaseError);
            }
        });


        updateFirebaseToken();
        builGoogleApiClient(view.getContext());
        createLocationRequest();
        checkPermission();


        return view;
    }


    //
    private void updateFirebaseToken() {
        Log.e(TAG, " updateFirebaseToken() ");
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tbl);
        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    private void builGoogleApiClient(Context context) {
        Log.e(TAG, "builGoogleApiClient()");
        googleApiClient = new GoogleApiClient.Builder(context)
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

    private void checkPermission() {
        Log.e(TAG, "=================================================================");
        Log.e(TAG, "                          checkPermission()                      ");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLocation();
        } else {
            verificarServicio();  // Si tiene los permisos - verficiar el  checkPlayService
        }
        Log.e(TAG, "=================================================================");
    }

    //
    private void requestPermissionsLocation() {
        ActivityCompat
                .requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_CODE);
    }

    private void verificarServicio() {
        if (checkPlayService()) {
            //builGoogleApiClient();
            createLocationRequest();
            //solo ocurre si esta activado
            if (Common.location_switch.isChecked()) {
                Log.e(TAG, " Off location ");
                displayLocationOnline();
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

    //
    private void displayLocationOnline() {
        Log.e(TAG, "=================================================================");
        Log.e(TAG, "                          displayLocationOnline()                      ");
        try {
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
                                    Log.e(TAG, " Common.mLastLocation = " + Common.mLastLocation);
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

                                        geoFireLocationDoctor.setLocation(firebaseUserUID, new GeoLocation(latitude, longitud), new GeoFire.CompletionListener() {
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
                                                } catch (Exception e) {
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
    //

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
        displayLocationOnline();
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
        displayLocationOnline();
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
                        //builGoogleApiClient();
                        createLocationRequest();
                        if (Common.location_switch.isChecked()) {
                            displayLocationOnline();
                            Log.e(TAG, "displayLocationOnline()" + "onRequestPermissionsResult");
                        }
                    }
                }
            }
        }

    }

    //
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
