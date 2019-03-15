package com.cudpast.app.doctor.doctorApp.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.cudpast.app.doctor.doctorApp.Business.DoctorBooking;
import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    //son dos casos
    //1.El usuario enviar una solicitud de atencion
    //2.El usuario cancela en cualquier momento la solicutud de atencion
    //primer plano  Notificacion
    //segundo plano Data
    public static final String TAG = MyFirebaseMessaging.class.getSimpleName();
    private static final String CHANNEL_ID = "MyMessagin";
    private static final int NOTIFICATION_ID = 9;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "                 MyFirebaseMessaging                    ");
        final String title = remoteMessage.getNotification().getTitle();
        final String body = remoteMessage.getNotification().getBody();

        if (title.equalsIgnoreCase("el usuario ha cancelado")) {
            Log.e(TAG, "==========================el usuario ha cancelado==============================");
            Intent intent = new Intent(this, DoctorHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        } else if (title.equalsIgnoreCase("CUDPAST")) {
            Log.e(TAG, "============================CUDPAST============================");
            String pToken            = remoteMessage.getData().get("title").toString();
            String json_lat_log      = remoteMessage.getData().get("descripcion").toString();
            String dToken            = remoteMessage.getData().get("extradata").toString();
            LatLng customer_location = new Gson().fromJson(json_lat_log, LatLng.class);

            Log.e(TAG, " title : " + title);
            Log.e(TAG, " body : " + body);
            Log.e(TAG, " pToken : " + pToken);
            Log.e(TAG, " dToken : " + dToken);
            Log.e(TAG, " customer_location : " + customer_location.latitude + " , " + customer_location.longitude);

            Intent resultIntent = new Intent(this, DoctorBooking.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtra("lat", customer_location.latitude);
            resultIntent.putExtra("lng", customer_location.longitude);
            resultIntent.putExtra("tokenPaciente", pToken);
            resultIntent.putExtra("tokenDoctor", pToken);
            startActivity(resultIntent);


//------------------------->
//            Intent notifyIntent = new Intent(this, DoctorBooking.class);
//            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            notifyIntent.putExtra("lat", customer_location.latitude);
//            notifyIntent.putExtra("lng", customer_location.longitude);
//            notifyIntent.putExtra("tokenPaciente", pToken);
//            notifyIntent.putExtra("tokenDoctor", pToken);
//            // Set the Activity to start in a new, empty task
//            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            // Create the PendingIntent
//            PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//            int color = getResources().getColor(R.color.colorRed);
//            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID);
//
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//            notification
//                    .setContentTitle(title)
//                    .setSmallIcon(R.drawable.ic_hospital)
//                    .setContentIntent(notifyPendingIntent)
//                    .setContentText(body);
//
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, " ", NotificationManager.IMPORTANCE_HIGH);
//                notificationChannel.setDescription("SLINKTER CHANNEL");
//                notification.setLargeIcon(BitmapFactory.decodeResource(
//                        getResources(),
//                        R.drawable.ic_doctor));
//                notificationChannel.enableLights(true);
//
//                notificationChannel.setLightColor(Color.RED);
//                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
//                notificationChannel.enableLights(true);
//                mNotificationManager.createNotificationChannel(notificationChannel);
//            } else {
//                notification
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setColor(color)
//                        .setLargeIcon(BitmapFactory.decodeResource(
//                                getResources(),
//                                R.drawable.ic_doctor))
//                        .setVibrate(new long[]{100, 250})
//                        .setLights(Color.YELLOW, 500, 5000)
//                        .setAutoCancel(true);
//            }
//
//
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//            notificationManager.notify(NOTIFICATION_ID, notification.build());

//<-------------------------

        }


    }


}

