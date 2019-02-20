package com.cudpast.app.doctor.doctorApp.Service;

import android.content.Intent;
import android.util.Log;

import com.cudpast.app.doctor.doctorApp.Business.CustomerCallActivity;
import com.cudpast.app.doctor.doctorApp.Model.Token;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    public static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //
        Log.e(TAG, "========================================================");
        Log.e(TAG, "                 MyFirebaseMessaging                    ");
        Log.e(TAG, "        onMessageReceived - RemoteMessage               ");
        String from = remoteMessage.getFrom();
        Log.e(TAG, "from:" + from);
       //
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notificacion : getTitle " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "Notificacion : getBody " + remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Notificacion : getData " + remoteMessage.getData()); // <--- I need to receive this from android device
        }
        Log.e(TAG, "========================================================");
        //

        LatLng customer_location = new Gson().fromJson(remoteMessage.getNotification().getBody(), LatLng.class);
        Token pacienteToken = new Gson().fromJson(remoteMessage.getNotification().getBody(), Token.class);


        Intent intent = new Intent(getBaseContext(), CustomerCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("lat", customer_location.latitude);
        intent.putExtra("lng", customer_location.longitude);
        intent.putExtra("customer", remoteMessage.getNotification().getTitle());
        intent.putExtra("tokenPaciente", remoteMessage.getNotification().getTitle());


//        Log.e(TAG, "From  : " + remoteMessage.getFrom());
//        Log.e(TAG, "Notification Message title  tokenPaciente : " + remoteMessage.getNotification().getTitle().toString());
//        Log.e(TAG, "Notification Message Body  lat/lng: " + remoteMessage.getNotification().getBody().toString());
//        Log.e(TAG, "Notification customer_location : " + customer_location);

        startActivity(intent);
    }
}

