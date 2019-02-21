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

        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {

            Log.e(TAG, "========================================================");
            Log.e(TAG, "                 MyFirebaseMessaging                    ");
            mostrarNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            String pToken = remoteMessage.getData().get("title").toString();
            LatLng customer_location2 = new Gson().fromJson(remoteMessage.getData().get("descripcion").toString(), LatLng.class);



            Intent intent = new Intent(getBaseContext(), CustomerCallActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("lat", customer_location2.latitude);
            intent.putExtra("lng", customer_location2.longitude);
            intent.putExtra("tokenPaciente", pToken);//
            startActivity(intent);
            Log.e(TAG, "========================================================");
        }
    }


    public void mostrarNotification(String s1 , String s2) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"hola")
                .setSmallIcon(R.drawable.ic_hospital)
                .setContentTitle(s1)
                .setContentText(s2)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }


}

