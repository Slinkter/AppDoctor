package com.cudpast.app.doctor.doctorApp.Business;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.FCMResponse;
import com.cudpast.app.doctor.doctorApp.Model.Notification;
import com.cudpast.app.doctor.doctorApp.Model.Sender;
import com.cudpast.app.doctor.doctorApp.Model.Token;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Remote.IFCMService;
import com.cudpast.app.doctor.doctorApp.Remote.IGoogleAPI;
import com.cudpast.app.doctor.doctorApp.Soporte.DirectionJSONParser;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorAcepta extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private static String TAG = "DoctorAcepta";

    //Google Play Service -->
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private GoogleApiClient mGoogleApiCliente;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    //Google Play Service <--

    private GoogleMap mMap;
    double pacienteLat, pacienteLng;
    String customerId;

    public Circle riderMarker;
    public Marker driverMarker;
    Polyline direction;
    IGoogleAPI mService;

    IFCMService mFCMService;
    GeoFire geoFire;

    //parte 19

    Button btnStartTrip;
    Location pickupLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_tracking);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDoctorTracking);
        mapFragment.getMapAsync(this);


        if (getIntent() != null) {
            pacienteLat = getIntent().getDoubleExtra("pacienteLat", -1.0);
            pacienteLng = getIntent().getDoubleExtra("pacienteLng", -1.0);
            customerId = getIntent().getStringExtra("sIdTokenPaciente");
        }
        mService = Common.getGoogleAPI();
        mFCMService = Common.getIFCMService();
        setUpLocation();


        btnStartTrip = findViewById(R.id.btnStartTip);

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cambia el texto
//                if (btnStartTrip.getText().equals("Empenzar")) {
//                    pickupLocation = Common.mLastLocation;// Inicia servicio
//                    btnStartTrip.setText("Avisar");
//                    //metodo dibujo
//
//                } else if (btnStartTrip.getText().equals("Avisar")) {
//                    calculateCashFee(pickupLocation, Common.mLastLocation);
//                    // cuando finaliza el transporte  pickup sigo con el valor del primer if
//                    // y mLastLocation tiene el ultimo valor
//
//                }
                // solo se activa cuando llegar el doctor para enviar un mensaje(Notificacion)
                avisarAlPaciente();


            }
        });



    }

    private void avisarAlPaciente() {


    }



    private void setUpLocation() {

        if (checkPlayService()) {
            builGoogleApiClient();
            createLocationRequest();
            displayLocation();
        }

    }

    private void builGoogleApiClient() {
        mGoogleApiCliente = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiCliente.connect();
    }

    private void createLocationRequest() {
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
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {

            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.e("error", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("error", "Can't find style. Error: ", e);
        }

        mMap = googleMap;

        if (direction != null) {
            direction.remove();//remote old direction

        }

        riderMarker = mMap.addCircle(new CircleOptions()
                .center(new LatLng(pacienteLat, pacienteLng))
                .radius(50)// 50 metros 5  000000000000000
                .strokeColor(Color.GREEN)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f));

        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(Common.TB_AVAILABLE_DOCTOR));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(pacienteLat, pacienteLng), 0.05f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                sendArriveNotification(customerId);
                btnStartTrip.setEnabled(true);
                // btnStartTrip.setText("REVISAR");
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void sendArriveNotification(String customerId) {

        Token token = new Token(customerId);
        String tokenpaciente = token.getToken();
        String titile = "Arrived";
        String body = String.format("el doctor %s ha llegado", Common.currentUser.getFirstname());
        Notification notification = new Notification(titile, body);

        Sender sender = new Sender(tokenpaciente, notification );

        mFCMService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if (response.body().success != 1) {
                    Toast.makeText(DoctorAcepta.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }

        Common.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCliente);
        if (Common.mLastLocation != null) {
            final double latitude = Common.mLastLocation.getLatitude();
            final double longitud = Common.mLastLocation.getLongitude();

            if (driverMarker != null && direction != null) {
                driverMarker.remove();
                direction.remove();
            }
            if (direction != null) {
                direction.remove();//remote old direction

            }

            LatLng doctorlatlng = new LatLng(latitude, longitud);
            MarkerOptions doctorMO = new MarkerOptions()
                    .position(doctorlatlng)
                    .title("USTED")
                    .icon(BitmapDoctorApp(DoctorAcepta.this, R.drawable.ic_doctorapp));

            driverMarker = mMap.addMarker(doctorMO);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(doctorlatlng, 17.0f));


            getDirection();
            Log.e(TAG, "displayLocation() :  Common.mLastLocation :" + longitud + " , " + latitude);

        } else {
            Log.d(TAG, "displayLocation()  : Error " + "Cannot get your location");
        }

    }

    private void getDirection() {
        Log.e(TAG, "=============================================================");
        Log.e(TAG, "                     getDirection()                          ");

        LatLng currentPosition = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());
        String requestApi = null;

        try {

            requestApi =
                    "https://maps.googleapis.com/maps/api/directions/json?" +
                            "mode=driving&" +
                            "transit_routing_preference=less_driving&" +
                            "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                            "destination=" + pacienteLat + "," + pacienteLng + "&" +
                            "key=" + "AIzaSyCZMjdhZ3FydT4lkXtHGKs-d6tZKylQXAA";

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {

                                if (direction != null) {
                                    direction.remove();//remote old direction

                                }

                                new getDireccionParserTask().execute(response.body().toString());


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(DoctorAcepta.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {

        }

    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiCliente, mLocationRequest, this);
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
    //.
    private class getDireccionParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        ProgressDialog mDialog = new ProgressDialog(DoctorAcepta.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Actualizando a Ubicación...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject object;
            List<List<HashMap<String, String>>> router = null;
            try {
                object = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                router = parser.parse(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return router;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {

            mDialog.dismiss();
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (int i = 0; i < lists.size(); i++) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = lists.get(i);
                //-->
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                //<--
                polylineOptions.addAll(points);
                polylineOptions.width(5);
                polylineOptions.color(Color.MAGENTA);
                polylineOptions.geodesic(true);
            }

            direction = mMap.addPolyline(polylineOptions);


        }
    }
}
