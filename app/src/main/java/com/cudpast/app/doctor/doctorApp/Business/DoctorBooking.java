package com.cudpast.app.doctor.doctorApp.Business;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.cudpast.app.doctor.doctorApp.Soporte.Notification;
import com.cudpast.app.doctor.doctorApp.Soporte.Sender;
import com.cudpast.app.doctor.doctorApp.Soporte.Token;
import com.cudpast.app.doctor.doctorApp.Model.UserPaciente;
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
    // Caso 1
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

    private DatabaseReference tb_Info_Paciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_booking);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapCustomerCall);
        mapFragment.getMapAsync(this);
        getSupportActionBar().hide();

        tb_Info_Paciente = FirebaseDatabase.getInstance().getReference(Common.TB_INFO_PACIENTE);
        tb_Info_Paciente.keepSynced(true);

        btnCancel = findViewById(R.id.btn_decline_booking);
        btnAccept = findViewById(R.id.btn_accept_booking);

        mService = Common.getGoogleAPI();
        mFCMService = Common.getIFCMService();
        auth = FirebaseAuth.getInstance();

        textPaciente = findViewById(R.id.textPaciente);
        textAddress = findViewById(R.id.txtAddress);
        textTime = findViewById(R.id.txtTime);
        textDistance = findViewById(R.id.txtDistance);

        if (Common.location_switch == null) {
            Log.e(TAG, "Common.location_switch : NULL");
        } else {
            Common.location_switch.toggle();
        }

        if (getIntent() != null) {

            title = getIntent().getStringExtra("title");
            body = getIntent().getStringExtra("body");
            pToken = getIntent().getStringExtra("pToken");
            dToken = getIntent().getStringExtra("dToken");
            lat = getIntent().getDoubleExtra("lat", -1.0);
            lng = getIntent().getDoubleExtra("lng", -1.0);
            pacienteUID = getIntent().getStringExtra("pacienteUID");
            //Get Paciente
            getDirection(lat, lng, pacienteUID);
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

                if (!TextUtils.isEmpty(pToken))
                    cancelBooking(pToken);
            }
        });


    }

    //.
    private void aceptBooking(final String  sIdTokenPaciente) {
        Log.e(TAG, "==========================================");
        Log.e(TAG, "                AceptBooking              ");
        //
        final SpotsDialog waitingDialog = new SpotsDialog(DoctorBooking.this, R.style.DialogUpdateDoctorEnviando);
        waitingDialog.show();
        //Enviar Notificacion hacia el paciente
        String doctorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Data data = new Data("APP Doctor", "Acepta","",doctorUID , "","");
        Sender sender = new Sender(sIdTokenPaciente,  data);
        //todo : esto nos ayuda a controlar el envio de mensaje o notificacion
        //todo : se debe crear una db para controlar la session
        mFCMService
                .sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                        Log.e(TAG, "response.body().success       : " + response.body().success);
                        Log.e(TAG, "response.body().results       : " + response.body().results.get(0));
                        Log.e(TAG, "response.body().canonical_ids : " + response.body().canonical_ids);
                        Log.e(TAG, "response.body().multicast_id  : " + response.body().multicast_id);

                        if (response.body().success == 1 ) {
                            waitingDialog.dismiss();
                            Log.e(TAG, "onResponse: success");
                            Intent intent = new Intent(DoctorBooking.this, DoctorRoad.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            doclat = Common.mLastLocation.getLatitude();
                            doclng = Common.mLastLocation.getLongitude();
                            //APP Doctor
                            intent.putExtra("doclat", doclat);
                            intent.putExtra("doclng", doclng);
                            //APP Paciente
                            intent.putExtra("pacienteLat", lat);
                            intent.putExtra("pacienteLng", lng);
                            intent.putExtra("sIdTokenPaciente", sIdTokenPaciente);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure : " + t.getMessage());
                        waitingDialog.dismiss();
                        Intent intent = new Intent(DoctorBooking.this, DoctorError.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });


    }

    //.
    private void cancelBooking(String IdToken) {
        Log.e(TAG, "==========================================");
        Log.e(TAG, "                cancelBooking             ");
        //
        final SpotsDialog waitingDialog = new SpotsDialog(DoctorBooking.this, R.style.DialogUpdateDoctorEnviando);
        waitingDialog.show();
        //Enviar Notificacion hacia el paciente
        Token token = new Token(IdToken);
        String title = "Cancel";
        String body = "el doctor ha cancelado la solicitud";
        //todo : mover la toggle (palanca) para que se active online
        Notification notification = new Notification(title, body);
        Sender sender = new Sender(token.getToken(), notification);

        Log.e(TAG, "token        : " + token);
        Log.e(TAG, "notification : " + notification);
        Log.e(TAG, "sender       : " + sender);

        mFCMService
                .sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success == 1) {
                            waitingDialog.dismiss();
                            Log.e(TAG, "response.body().success : " + response.body().success);
                            Toast.makeText(DoctorBooking.this, "Cita no atendida", Toast.LENGTH_SHORT).show();

                        } else {
                            waitingDialog.dismiss();
                            Toast.makeText(DoctorBooking.this, "Failed ! ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                        Log.e(TAG, "error : " + t.getMessage());
                    }

                });
        finish();
    }

    //Cargar duración distancia y direccion final
    private void getDirection(double lat, double lng, String mpacienteUID) {

        tb_Info_Paciente
                .child(mpacienteUID)
                .orderByKey()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserPaciente userPaciente = dataSnapshot.getValue(UserPaciente.class);
                        Common.currentPaciente = userPaciente;
                        textPaciente.setText(userPaciente.getNombre() + " " + userPaciente.getApellido());
                        Log.e(TAG, " currentPaciente :" + Common.currentPaciente.getNombre());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, " databaseError : " + databaseError.getMessage());
                    }
                });


        String requestApi = null;

        try {
            requestApi =
                    "https://maps.googleapis.com/maps/api/directions/json?" +
                            "mode=driving&" +
                            "transit_routing_preference=less_driving&" +
                            "origin=" + Common.mLastLocation.getLatitude() + "," + Common.mLastLocation.getLongitude() + "&" +
                            "destination=" + lat + "," + lng + "&" +
                            "key=" + "AIzaSyCZMjdhZ3FydT4lkXtHGKs-d6tZKylQXAA";

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {

                                JSONObject jsonObject = new JSONObject(response.body().toString());

                                JSONArray routes = jsonObject.getJSONArray("routes");
                                JSONObject object = routes.getJSONObject(0);
                                JSONArray legs = object.getJSONArray("legs");
                                JSONObject legsObject = legs.getJSONObject(0);
                                //
                                JSONObject distance = legsObject.getJSONObject("distance");
                                textDistance.setText(distance.getString("text"));
                                //
                                JSONObject time = legsObject.getJSONObject("duration");
                                textTime.setText(time.getString("text"));
                                //
                                String address = legsObject.getString("end_address");
                                textAddress.setText(address);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(DoctorBooking.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            //59:06
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }

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
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Paciente")
                .icon(BitmapDoctorApp(DoctorBooking.this, R.drawable.ic_boy_svg)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

    }

    //.
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
}
