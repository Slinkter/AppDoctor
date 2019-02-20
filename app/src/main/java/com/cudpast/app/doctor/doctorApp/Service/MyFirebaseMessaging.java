package com.cudpast.app.doctor.doctorApp.Service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cudpast.app.doctor.doctorApp.Business.CustomerCallActivity;
import com.cudpast.app.doctor.doctorApp.Model.Token;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    public static final String TAG = "MyFirebaseMessaging";



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);


        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
            Log.e(TAG, "========================================================");
            Log.e(TAG, "                 MyFirebaseMessaging                    ");
            Log.e(TAG, "        onMessageReceived - RemoteMessage               ");
            Log.e(TAG, "Notificacion : getData " + remoteMessage.getData());

            String from = remoteMessage.getFrom();
            String title = remoteMessage.getData().get("title").toString();
            String descripcion = remoteMessage.getData().get("descripcion").toString();

            Log.e(TAG, "title:" + title);
            Log.e(TAG, "descripcion:" + descripcion);

            Log.e(TAG, "from:" + from);
            Log.e(TAG, "Notificacion : getTitle " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "Notificacion : getBody " + remoteMessage.getNotification().getBody());
            Log.e(TAG, "Notificacion : getData " + remoteMessage.getData()); // <--- I need to receive this from android device
//            mostrarNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            mostrarNotification();
            LatLng customer_location = new Gson().fromJson(remoteMessage.getNotification().getBody(), LatLng.class);
            Intent intent = new Intent(getBaseContext(), CustomerCallActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("lat", customer_location.latitude);
            intent.putExtra("lng", customer_location.longitude);
            intent.putExtra("tokenPaciente", remoteMessage.getNotification().getTitle());
            startActivity(intent);
            Log.e(TAG, "========================================================");
        }




    }


    public void mostrarNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"hola")
                .setSmallIcon(R.drawable.ic_hospital)
                .setContentTitle("Cliente")
                .setContentText("cliente")
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }


}

