package com.cudpast.app.doctor.doctorApp.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "                 MyFirebaseMessaging                    ");
        final String title = remoteMessage.getNotification().getTitle();
        final String body = remoteMessage.getNotification().getBody();

        if (title.equalsIgnoreCase("el usuario ha cancelado")) {
            Log.e(TAG, "==========================el usuario ha cancelado==============================");
            Intent intent = new Intent(getBaseContext(), DoctorHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (title.equalsIgnoreCase("CUDPAST")) {
            Log.e(TAG, "============================CUDPAST============================");
            String pToken = remoteMessage.getData().get("title").toString();
            String json_lat_log = remoteMessage.getData().get("descripcion").toString();
            String dToken = remoteMessage.getData().get("extradata").toString();
            LatLng customer_location = new Gson().fromJson(json_lat_log, LatLng.class);

            Log.e(TAG, " title : " + title);
            Log.e(TAG, " body : " + body);
            Log.e(TAG, " pToken : " + pToken);
            Log.e(TAG, " dToken : " + dToken);
            Log.e(TAG, " customer_location : " + customer_location.latitude + " , " + customer_location.longitude);


            Intent intent = new Intent(getBaseContext(), DoctorBooking.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("lat", customer_location.latitude);
            intent.putExtra("lng", customer_location.longitude);
            intent.putExtra("tokenPaciente", pToken);
            intent.putExtra("tokenDoctor", pToken);
            startActivity(intent);

//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                String NOTIFICATION_CHANNEL_ID = "CUDPAST";
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "", NotificationManager.IMPORTANCE_DEFAULT);
//                    notificationChannel.setDescription("SLINKTER CHANNEL");
//                    notificationChannel.enableLights(true);
//                    notificationChannel.setLightColor(Color.BLUE);
//                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
//                    notificationChannel.enableLights(true);
//                    notificationManager.createNotificationChannel(notificationChannel);
//                }
//
//
//                int color = getResources().getColor(R.color.colorRed);
//
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//                builder
//                        .setSmallIcon(R.drawable.ic_hospital)
//                        .setContentTitle(title)
//                        .setContentText(body)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setContentIntent(pendingIntent)
//                        .setContentInfo("Info")
//                        .setColor(color)
//                        .setAutoCancel(true);
//
//                Notification notification = builder.build();
//                NotificationManagerCompat.from(this).notify(0,notification);



        }




    }


}

