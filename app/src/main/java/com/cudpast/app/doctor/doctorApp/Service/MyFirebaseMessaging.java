package com.cudpast.app.doctor.doctorApp.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cudpast.app.doctor.doctorApp.Business.DoctorBooking;
import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    public static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        //parte 11 mejora de diseño

        if (remoteMessage.getData().size() > 0) {

            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

//            if (remoteMessage.getNotification() != null) {
                //son dos casos
                //1.El usuario enviar una solicitud de atencion
                //2.El usuario cancela en cualquier momento la solicutud de atencion
                Log.e(TAG, "========================================================");
                Log.e(TAG, "                 MyFirebaseMessaging                    ");
                String title = remoteMessage.getNotification().getTitle();
                Log.e(TAG, title);

                if (title.equalsIgnoreCase("el usuario ha cancelado")) {
                    Intent intent = new Intent(getBaseContext(), DoctorHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (title.equalsIgnoreCase("CUDPAST")) {

                    String body = remoteMessage.getNotification().getBody();
                    String pToken = remoteMessage.getData().get("title").toString();
                    String json_lat_log = remoteMessage.getData().get("descripcion").toString();
                    String dToken = remoteMessage.getData().get("extradata").toString();
                    LatLng customer_location = new Gson().fromJson(json_lat_log, LatLng.class);

                    Log.e(TAG, title);
                    Log.e(TAG, body);
                    Log.e(TAG, pToken);
                    Log.e(TAG, dToken);
                    Log.e(TAG, " --> " + customer_location.latitude + " , " + customer_location.longitude);

                    Intent intent = new Intent(getBaseContext(), DoctorBooking.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("lat", customer_location.latitude);
                    intent.putExtra("lng", customer_location.longitude);
                    intent.putExtra("tokenPaciente", pToken);
                    intent.putExtra("tokenDoctor", pToken);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "hola")
                            .setSmallIcon(R.drawable.ic_hospital)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());

                    startActivity(intent);
                    Log.e(TAG, "========================================================");
                }
            //}

//            sendNotification(remoteMessage.getData().get("message"));
        }
        // Check if message contains a notification payload.
        else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }


    }





    private void sendNotification(String message) {


    }



}

