package com.cudpast.app.doctor.doctorApp.Business;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Soporte.Data;
import com.cudpast.app.doctor.doctorApp.Soporte.FCMResponse;
import com.cudpast.app.doctor.doctorApp.Soporte.Sender;
import com.cudpast.app.doctor.doctorApp.Model.PacienteProfile;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Remote.IFCMService;
import com.cudpast.app.doctor.doctorApp.Remote.IGoogleAPI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorBooking extends AppCompatActivity implements OnMapReadyCallback {

    private static String TAG = DoctorBooking.class.getSimpleName();
    private IGoogleAPI mService;
    private IFCMService mFCMService;
    private TextView textTime, textAddress, textDistance, textPaciente;
    private Button btnCancel, btnAccept;
    public FirebaseAuth auth;
    public String title, body, pToken, dToken, pacienteUID;
    public double lat, lng;
    double doclat, doclng;
    private GoogleMap mMap;
    public DatabaseReference tb_Info_Paciente;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_booking);
        getSupportActionBar().hide();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapCustomerCall);
        mapFragment.getMapAsync(this);

        if (getIntent() != null) {
            tb_Info_Paciente = FirebaseDatabase.getInstance().getReference(Common.TB_INFO_PACIENTE);
            tb_Info_Paciente.keepSynced(true);

            mService = Common.getGoogleAPI();
            mFCMService = Common.getIFCMService();
            auth = FirebaseAuth.getInstance();

            textPaciente = findViewById(R.id.tv_namePaciente);
            textAddress = findViewById(R.id.tv_addressPaciente);
            textTime = findViewById(R.id.tv_timePaciente);
            textDistance = findViewById(R.id.tv_distancePaciente);

            btnCancel = findViewById(R.id.btn_decline_booking);
            btnAccept = findViewById(R.id.btn_accept_booking);

            if (Common.location_switch == null) {
                Log.e(TAG, "Common.location_switch : NULL");
            } else {
                Common.location_switch.toggle();// se pone en offline cuando se el doctor acepta la consulta medica
                Log.e(TAG, "se  puso en offline al doctor ");
            }
            Log.e(TAG, "======================================================");
            Log.e(TAG, "--------------> sendRequestDoctor                    ");
            title = getIntent().getStringExtra("title");
            body = getIntent().getStringExtra("body");
            pToken = getIntent().getStringExtra("pToken");
            dToken = getIntent().getStringExtra("dToken");
            lat = getIntent().getDoubleExtra("paciente_lat", -1.0);
            lng = getIntent().getDoubleExtra("paciente_lng", -1.0);
            pacienteUID = getIntent().getStringExtra("pacienteUID");
            Log.e(TAG, "Title  = " + title);
            Log.e(TAG, "Body  = " + body);
            Log.e(TAG, "ptoken  = " + pToken);
            Log.e(TAG, "dtoken  = " + dToken);
            Log.e(TAG, "paciente_lat  = " + lat);
            Log.e(TAG, "paciente_lng  = " + lng);
            Log.e(TAG, "pacienteUID  = " + pacienteUID);
            if (pacienteUID != null) {
                getDirection(lat, lng, pacienteUID);
                Log.e(TAG, "pacienteUID   es   " + pacienteUID);
            }else{
                Log.e(TAG, "pacienteUID   es null  ");
            }
        }

        //.
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aceptBooking(pToken);
            }
        });
        //.
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(pToken)) {
                    cancelBooking(pToken);
                }
            }
        });


    }

    //.
    private void aceptBooking(final String tokenPaciente) {
        //todo : esto nos ayuda a controlar el envio de mensaje o notificacion
        //todo : se debe crear una db para controlar la session
        Log.e(TAG, "==========================================");
        Log.e(TAG, "                AceptBooking              ");
        //
        final SpotsDialog waitingDialog = new SpotsDialog(DoctorBooking.this, R.style.DialogUpdateDoctorEnviando);
        waitingDialog.show();
        //Enviar Notificacion hacia el paciente
        String doctorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String tokenPaciente1 = "";
        Data data = new Data("APP Doctor", "Acepta", "", doctorUID, "", "");
        Sender sender = new Sender(tokenPaciente, data);
        //
        mFCMService
                .sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success == 1) {
                            waitingDialog.dismiss();
                            Log.e(TAG, "onResponse: success");
                            doclat = Common.mLastLocation.getLatitude();
                            doclng = Common.mLastLocation.getLongitude();
                            //
                            Intent intent = new Intent(DoctorBooking.this, DoctorRoad.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //APP Doctor
                            intent.putExtra("doclat", doclat);
                            intent.putExtra("doclng", doclng);
                            //APP Paciente
                            intent.putExtra("pacienteLat", lat);
                            intent.putExtra("pacienteLng", lng);
                            intent.putExtra("tokenPaciente", tokenPaciente);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure : " + t.getMessage());
                        waitingDialog.dismiss();
                        Intent intent = new Intent(DoctorBooking.this, DoctorError.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    //.
    private void cancelBooking(final String tokenPaciente) {
        Log.e(TAG, "==========================================");
        Log.e(TAG, "                cancelBooking             ");
        final SpotsDialog waitingDialog = new SpotsDialog(DoctorBooking.this, R.style.DialogUpdateDoctorEnviando);
        waitingDialog.show();
        //Enviar Notificacion hacia el paciente
        String title = "El doctor ha cancelado la solicitud";
        String body = "rechaza";
        //
        Data data = new Data(title, body, "", "", "", "");
        Sender sender = new Sender(tokenPaciente, data);
        //
        Log.e(TAG, "token        : " + tokenPaciente);
        Log.e(TAG, "data         : " + data);
        Log.e(TAG, "sender       : " + sender);
        //
        mFCMService
                .sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success == 1) {
                            waitingDialog.dismiss();
                            Log.e(TAG, "response.body().success : " + response.body().success);
                            Toast.makeText(DoctorBooking.this, "Cita no atendida", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            waitingDialog.dismiss();
                            Toast.makeText(DoctorBooking.this, "error al responder", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DoctorBooking.this, DoctorError.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                        waitingDialog.dismiss();
                        Log.e(TAG, "onFailure : " + t.getMessage());
                        Intent intent = new Intent(DoctorBooking.this, DoctorError.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                });
        // se cierra la actividad y regresa
        // finish();
        //
    }

    //
    private void getDirection(final double lat, final double lng, String uid_paciente) {
        Log.e(TAG, "==========================================");
        Log.e(TAG, " getDirection ");
        Log.e(TAG, " tb_Info_Paciente =  " + tb_Info_Paciente);

        final String url_requestApiGoogleMaps =
                "https://maps.googleapis.com/maps/api/directions/json?" +
                "mode=driving&" +
                "transit_routing_preference=less_driving&" +
                "origin=" + Common.mLastLocation.getLatitude() + "," + Common.mLastLocation.getLongitude() + "&" +
                "destination=" + lat + "," + lng + "&" +
                "key=" + "AIzaSyCZMjdhZ3FydT4lkXtHGKs-d6tZKylQXAA";

        Log.e(TAG, " url_requestApiGoogleMaps = " + url_requestApiGoogleMaps);

        tb_Info_Paciente
                .orderByKey()
                .equalTo(uid_paciente)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot post : dataSnapshot.getChildren()) {

                            final PacienteProfile pacienteProfile = post.getValue(PacienteProfile.class);
                            Log.e(TAG, " pacienteProfile =  " + pacienteProfile.getFirstname());

                            mService.getPath(url_requestApiGoogleMaps)
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            Log.e(TAG, " pre - try-catch");
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.body());
                                                JSONArray routes = jsonObject.getJSONArray("routes");
                                                JSONObject object = routes.getJSONObject(0);
                                                JSONArray legs = object.getJSONArray("legs");
                                                JSONObject legsObject = legs.getJSONObject(0);
                                                Log.e(TAG, "onResponse : jsonObject =" + jsonObject);
                                                //
                                                JSONObject distance = legsObject.getJSONObject("distance");
                                                textDistance.setText(distance.getString("text"));
                                                Log.e(TAG, " distance = " + distance);
                                                //
                                                JSONObject time = legsObject.getJSONObject("duration");
                                                textTime.setText(time.getString("text"));
                                                Log.e(TAG, " time = " + time);
                                                //
                                                String address = legsObject.getString("end_address");
                                                textAddress.setText(address);
                                                Log.e(TAG, " address = " + address);
                                                //
                                                Common.currentPaciente = pacienteProfile;
                                                textPaciente.setText(pacienteProfile.getFirstname() + " " + pacienteProfile.getLastname());
                                                Log.e(TAG, "currentPaciente =" + Common.currentPaciente.getFirstname());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Log.e(TAG, "error try-catch = " + e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            Log.e(TAG, " onFailure = " + t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, " databaseError : " + databaseError.getMessage());
                    }
                });
    }

    //.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        mMap = googleMap;
        //
        LatLng geoPaciente = new LatLng(lat, lng);
        mMap
                .addMarker(new MarkerOptions()
                        .position(geoPaciente)
                        .title("Paciente")
                        .icon(BitmapDoctorApp(DoctorBooking.this, R.drawable.ic_boy_svg)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(geoPaciente, 16));
    }

    //.
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
