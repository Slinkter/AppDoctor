package com.cudpast.app.doctor.doctorApp.Business;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.Model.FCMResponse;
import com.cudpast.app.doctor.doctorApp.Model.Notification;
import com.cudpast.app.doctor.doctorApp.Model.Sender;
import com.cudpast.app.doctor.doctorApp.Model.Token;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Remote.IFCMService;
import com.cudpast.app.doctor.doctorApp.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCallActivity extends AppCompatActivity {

    TextView textTime, textAddress, textDistance;
    Button btnCancel, btnAccept;
    //  MediaPlayer mediaPlayer;
    IGoogleAPI mService;
    IFCMService mFCMService;
    String IdTokenPaciente;

    double lat,lng;

    double doclat,doclng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_call);

        mService = Common.getGoogleAPI();
        mFCMService = Common.getIFCMService();

        textAddress = findViewById(R.id.txtAddress);
        textTime = findViewById(R.id.txtTime);
        textDistance = findViewById(R.id.txtDistance);

        btnCancel = findViewById(R.id.btnDecline);
        btnAccept = findViewById(R.id.btnAccept);

        //Recibir token y la coordernadas de MyfirebaseMessaging

        if (getIntent() != null) {
            lat = getIntent().getDoubleExtra("lat", -1.0);
            lng = getIntent().getDoubleExtra("lng", -1.0);
            IdTokenPaciente = getIntent().getStringExtra("token");
            getDirection(lat, lng);

        }


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(IdTokenPaciente))
                    cancelBooking(IdTokenPaciente);
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aceptBooking();
            }
        });
    }

    private void cancelBooking(String IdToken) {

        Token token = new Token(IdToken);

        Notification notification = new Notification("Cancel", "el doctor ha cancelado la cita");
        Sender sender = new Sender(token.getToken(), notification);


        Log.e("CustomerCallActivity", "token        : ------->" + token);
        Log.e("CustomerCallActivity", "notification : ------->" + notification);
        Log.e("CustomerCallActivity", "sender       : ------->" + sender);

        mFCMService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {

                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                        Log.e("CustomerCallActivity", "response.body().success:--------->" + response.body().success);
                        if (response.body().success == 1) {
                            Toast.makeText(CustomerCallActivity.this, "Cita no atendida", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CustomerCallActivity.this, "Failed ! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                        Log.e("ERROR", t.getMessage());
                    }

                });
    }

    private void aceptBooking( ){
        Intent intent = new Intent(CustomerCallActivity.this,DoctorTracking.class);
        doclat= Common.mLastLocation.getLatitude() ;
        doclng= Common.mLastLocation.getLongitude();
        //APP Paciente
        intent.putExtra("lat",lat);
        intent.putExtra("lng",lng);
        intent.putExtra("customerId",IdTokenPaciente);
        //APP Doctor
        intent.putExtra("doclat",doclat);
        intent.putExtra("doclng",doclng);
        startActivity(intent);
        finish();
    }

    //Cargar duración distancia y direccion final
    private void getDirection(double lat, double lng) {

        String requestApi = null;

        try {

            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + Common.mLastLocation.getLatitude() + "," + Common.mLastLocation.getLongitude() + "&" +
                    "destination=" + lat + "," + lng + "&" +
                    "key=" + "AIzaSyCZMjdhZ3FydT4lkXtHGKs-d6tZKylQXAA";

            Log.e("CustomerCallActivity", "requestApi:--------->" + requestApi);

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
                            Toast.makeText(CustomerCallActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            //59:06
                        }
                    });


        } catch (Exception e) {

        }

    }


}
