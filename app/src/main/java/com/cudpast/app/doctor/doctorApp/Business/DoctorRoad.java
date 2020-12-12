package com.cudpast.app.doctor.doctorApp.Business;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;


import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.app.doctor.doctorApp.Activities.MainActivity;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Soporte.Data;
import com.cudpast.app.doctor.doctorApp.Soporte.FCMResponse;
import com.cudpast.app.doctor.doctorApp.Soporte.Sender;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorRoad extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private static String TAG = DoctorRoad.class.getSimpleName();
    //Google Play Service
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int PLAY_SERVICE_RES_REQUEST = 7001;
    public static final int UPDATE_INTERVAL = 5000;
    public static final int FATEST_INTERVAL = 3000;
    public static final int DISPLACEMENT = 10;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiCliente;
    private LocationRequest mLocationRequest;
    //
    double pacienteLat, pacienteLng;
    String idTokenPaciente;
    FusedLocationProviderClient ubicacion;
    Circle pacienteMarker;
    Marker driverMarker;
    IGoogleAPI mService;
    IFCMService mFCMService;
    GeoFire geoFire;
    String doctorUid;
    Button btn_ruta_cancelar;
    Dialog myDialog;
    DatabaseReference referenceService, doctorService, onlineRef;
    LocationCallback mLocationCallback;
    TextView id_tiempoDoctorRoad, id_distanciaDoctorRoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_road);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDoctorTracking);
        mapFragment.getMapAsync(this);
        //
        ubicacion = LocationServices.getFusedLocationProviderClient(this);
        btn_ruta_cancelar = findViewById(R.id.btn_ruta_cancelar);
        id_tiempoDoctorRoad = findViewById(R.id.id_tiempoDoctorRoad);
        id_distanciaDoctorRoad = findViewById(R.id.id_distanciaDoctorRoad);
        //
        myDialog = new Dialog(this);
        //
        mService = Common.getGoogleAPI();
        mFCMService = Common.getIFCMService();
        //
        if (getIntent() != null) {
            pacienteLat = getIntent().getDoubleExtra("pacienteLat", -1.0);
            pacienteLng = getIntent().getDoubleExtra("pacienteLng", -1.0);
            idTokenPaciente = getIntent().getStringExtra("tokenPaciente");
        }

        doctorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referenceService = FirebaseDatabase.getInstance().getReference(Common.TB_SERVICIO_DOCTOR_PACIENTE);
        doctorService = referenceService.child(doctorUid);

        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, " onDataChange       -----> " + dataSnapshot.getValue());
                doctorService.onDisconnect().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().goOnline();

        geoFire = new GeoFire(referenceService);
        setUpLocation();

        btn_ruta_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopupCancelar();
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.e("MainActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                }
            }

            ;

        };

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
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(Common.TB_SERVICIO_DOCTOR_PACIENTE));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(pacienteLat, pacienteLng), 0.05f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                ShowPopupNotification();
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

    private void getRoadRealTime() {
        Log.e(TAG, "=============================================================");
        Log.e(TAG, "                     getRoadRealTime()                       ");
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

            final LatLng doctorlatlng = new LatLng(currentPosition.latitude, currentPosition.longitude);

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {

                                driverMarker = mMap.addMarker(new MarkerOptions().position(doctorlatlng).title("USTED").icon(BitmapDoctorApp(DoctorRoad.this, R.drawable.ic_doctorapp)));

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(doctorlatlng, 16.0f));

                                new getDirectionRealTime().execute(response.body().toString());


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(DoctorRoad.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {

        }

    }

    private void startLocationUpdate() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiCliente, mLocationRequest, this);
    }

    private class getDirectionRealTime extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        ProgressDialog mDialog = new ProgressDialog(DoctorRoad.this);
        Polyline direction = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Actualizando su ubicación...");
            mDialog.show();
        }

        @SuppressLint("WrongThread")
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

            List<List<HashMap<String, String>>> router = null;
            try {
                JSONObject objecto = new JSONObject(strings[0]);

                DirectionJSONParser parser = new DirectionJSONParser();
                router = parser.parse(objecto);


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
                    String distance = point.get("distance");
                    String duration = point.get("duration");
                    LatLng position = new LatLng(lat, lng);
                    id_distanciaDoctorRoad.setText(distance);
                    id_tiempoDoctorRoad.setText(duration);
                    points.add(position);
                }
                //<--
                polylineOptions.addAll(points);
                polylineOptions.width(4);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                direction = mMap.addPolyline(polylineOptions);
            } else {
                Log.e(TAG, "ERROR ,   direction = mMap.addPolyline(polylineOptions);");
            }


        }
    }
    // Eventos - Mensaje

    public void ShowPopupCancelar() {

        //mostrar un display para cancelar el servicio
        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorRoad.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_cancelar, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_accept_cancelar, btn_decline_cancelar;
        btn_accept_cancelar = view.findViewById(R.id.btn_accept_cancelar);
        btn_decline_cancelar = view.findViewById(R.id.btn_decline_cancelar);

        btn_accept_cancelar
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Enviando cancelación del servicio", Toast.LENGTH_SHORT).show();
                        doctorService.onDisconnect().removeValue();
                        FirebaseDatabase.getInstance().goOffline();
                        cancelServiceOnRoad(idTokenPaciente);
                        dialog.dismiss();
                        finish();

                    }
                });

        btn_decline_cancelar
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });

        dialog.show();


    }

    public void ShowPopupNotification() {
        // cuando el doctor llega a la zona o direccion del paciente
        // se muestra cuando esta dentro del área en el metodo

        try {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.pop_up_notification, null);
            view.setKeepScreenOn(true);
            //
            AlertDialog.Builder builder = new AlertDialog.Builder(DoctorRoad.this);
            builder.setView(view);
            builder.setCancelable(false);
            //
            final AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button btn_sendNotification = view.findViewById(R.id.btn_send_notification);
            btn_sendNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendArriveNotification(idTokenPaciente);
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private void sendArriveNotification(final String tokenPaciente) {
        // esta en el metodo onMapReady>ShowPopupNotification> sendArriveNotification(idTokenPaciente);
        Log.e(TAG, "=====================================================");
        Log.e(TAG, "             sendArriveNotification                  ");

        Log.e(TAG, tokenPaciente);
        //Enviar Notificacion hacia el paciente
        String doctorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String title = String.format("el doctor %s ha llegado", Common.currentUserDoctor.getFirstname());
        String body = "Arrived";
        //
        Data data = new Data(title, body, "", doctorUID, "", "");
        Sender sender = new Sender(tokenPaciente, data);
        Log.e(TAG, " " + data);
        Log.e(TAG, " " + sender);
        Log.e(TAG, "OK");

        mFCMService
                .sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success == 1) {
                            Toast.makeText(DoctorRoad.this, "Se envio la notificación ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DoctorRoad.this, DoctorEnd.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(DoctorRoad.this, "Failed", Toast.LENGTH_SHORT).show();
                            Intent intentError = new Intent(DoctorRoad.this, DoctorError.class);
                            startActivity(intentError);
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                        Toast.makeText(DoctorRoad.this, "Failed", Toast.LENGTH_SHORT).show();
                        Intent intentError = new Intent(DoctorRoad.this, DoctorError.class);
                        startActivity(intentError);
                        finish();

                    }
                });
        Log.e(TAG, "=====================================================");
        ubicacion.removeLocationUpdates(mLocationCallback);
    }

    private void cancelServiceOnRoad(final String tokenPaciente) {

        Log.e(TAG, "==========================================");
        Log.e(TAG, "                cancelServiceOnRoad             ");
        //
        final SpotsDialog waitingDialog = new SpotsDialog(DoctorRoad.this, R.style.DialogUpdateDoctorEnviando);
        waitingDialog.show();
        //Enviar Notificacion hacia el paciente
        String title = "El doctor ha cancelado la solicitud";
        String body = "Cancel";
        //
        Data data = new Data(title, body, "", "", "", "");
        Sender sender = new Sender(tokenPaciente, data);
        //
        Log.e(TAG, "tokenPaciente    : " + tokenPaciente);
        Log.e(TAG, "data     : " + data);
        Log.e(TAG, "sender   : " + sender);
        //
        try {
            mFCMService
                    .sendMessage(sender)
                    .enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body().success == 1) {
                                waitingDialog.dismiss();
                                Log.e(TAG, "success : " + response.body().success);
                                Toast.makeText(getApplicationContext(), "Cita ha sido cancelado", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DoctorRoad.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            waitingDialog.dismiss();
                            Log.e(TAG, "error : al enviar la notificación " + t.getMessage());
                            Intent intent = new Intent(DoctorRoad.this, DoctorError.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                    });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }


    // Metodos de support
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

    // Metodos de Google Api
    private void setUpLocation() {
        if (checkPlayService()) {
            builGoogleApiClient();
            createLocationRequest();
            displayLocation();
        }
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

    private void displayLocation() {
        Log.e(TAG, "=================================================================");
        Log.e(TAG, "                          displayLocation()                      ");
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            ubicacion
                    .getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                final double latitude = location.getLatitude();
                                final double longitud = location.getLongitude();

                                Log.e(TAG, " firebaseUserUID : --> " + doctorUid);
                                Log.e(TAG, " Common.mLastLocation.getLatitude() : --> " + latitude);
                                Log.e(TAG, " Common.mLastLocation.getLongitude(): --> " + longitud);
                                //-->
                                //guardar cada vez que cambie la ubicacion del usario
                                geoFire
                                        .setLocation(doctorUid, new GeoLocation(latitude, longitud), new GeoFire.CompletionListener() {
                                            @Override
                                            public void onComplete(String key, DatabaseError error) {

                                                Log.e(TAG, "inserto en la geofire");
                                                Log.e(TAG, "key" + key);
                                                Log.e(TAG, "firebaseUserUID" + doctorUid);
                                                Log.e(TAG, "latitude" + latitude);
                                                Log.e(TAG, "longitud" + longitud);
                                                Log.e(TAG, "error" + error);

                                            }
                                        });
                                mMap.clear();
                                //Dibujar area del paciente
                                pacienteMarker = mMap.addCircle(new CircleOptions()
                                        .center(new LatLng(pacienteLat, pacienteLng))
                                        .radius(50)// 50 metros 5  000000000000000
                                        .strokeColor(Color.WHITE)
                                        .fillColor(0x220000FF)
                                        .strokeWidth(5.0f));
                                getRoadRealTime();

                                Log.e(TAG, "=================================================================");
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private BitmapDescriptor BitmapDoctorApp(Context context, @DrawableRes int vectorDrawableResourceId) {
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
