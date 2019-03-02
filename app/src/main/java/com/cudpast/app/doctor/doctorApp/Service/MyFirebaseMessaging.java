package com.cudpast.app.doctor.doctorApp.Service;

import android.content.Intent;
import android.util.Log;

import com.cudpast.app.doctor.doctorApp.Business.DoctorBooking;
import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    public static final String TAG = "MyFirebaseMessaging";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
            //son dos casos
            //1.El usuario enviar una solicitud de atencion
            //2.El usuario cancela en cualquier momento la solicutud de atencion
            Log.e(TAG, "========================================================");
            Log.e(TAG, "                 MyFirebaseMessaging                    ");
            String title = remoteMessage.getNotification().getTitle();
            Log.e(TAG, title);

            if (title.equalsIgnoreCase("el usuario ha cancelado")){
                Intent intent = new Intent(getBaseContext(), DoctorHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else if (title.equalsIgnoreCase("CUDPAST")){
                String body = remoteMessage.getNotification().getBody();
                String pToken = remoteMessage.getData().get("title").toString();
                String json_lat_log = remoteMessage.getData().get("descripcion").toString();
                String dToken = remoteMessage.getData().get("extradata").toString();

                LatLng customer_location = new Gson().fromJson(json_lat_log, LatLng.class);
                Log.e(TAG, title);
                Log.e(TAG, body);
                Log.e(TAG, pToken);
                Log.e(TAG, dToken);
                Log.e(TAG,  " --> " +customer_location.latitude + " , " + customer_location.longitude);

                Intent intent = new Intent(getBaseContext(), DoctorBooking.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("lat", customer_location.latitude);
                intent.putExtra("lng", customer_location.longitude);
                intent.putExtra("tokenPaciente", pToken);
                intent.putExtra("tokenDoctor", pToken);
                startActivity(intent);
                Log.e(TAG, "========================================================");
            }
        }
    }


//    public void mostrarNotification(String s1, String s2, String pToken, LatLng customer_location) {
//
//        Log.e(TAG, "========================================================");
//        Log.e(TAG, "                 MyFirebaseMessaging                    ");
//
//        Intent intent = new Intent(this, DoctorBooking.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("tokenPaciente", pToken);
//        intent.putExtra("lat", customer_location.latitude);
//        intent.putExtra("lng", customer_location.longitude);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "hola")
//                .setSmallIcon(R.drawable.ic_hospital)
//                .setContentTitle(s1)
//                .setContentText(s2)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notificationBuilder.build());
//        Log.e(TAG, "========================================================");
//    }


}

