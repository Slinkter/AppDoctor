package com.cudpast.app.doctor.doctorApp.Service;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.cudpast.app.doctor.doctorApp.Business.DoctorBooking;
import com.cudpast.app.doctor.doctorApp.Business.DoctorHome;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.R;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;
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

    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "                 MyFirebaseMessaging                    ");
        final String title = remoteMessage.getNotification().getTitle();
        final String body = remoteMessage.getNotification().getBody();

        if (title.equalsIgnoreCase("el usuario ha cancelado")) {
            Log.e(TAG, "        El usuario ha cancelado             ");
            Intent intent = new Intent(this, DoctorHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (title.equalsIgnoreCase("CUDPAST")) {
            Log.e(TAG, "            DoctorBooking              ");
            String pToken = remoteMessage.getData().get("title").toString();
            String json_lat_log = remoteMessage.getData().get("descripcion").toString();
            String dToken = remoteMessage.getData().get("extradata").toString();
            String pacienteUID = remoteMessage.getData().get("uidPaciente").toString();
            LatLng customer_location = new Gson().fromJson(json_lat_log, LatLng.class);

            Log.e(TAG, " title : " + title);
            Log.e(TAG, " body : " + body);
            Log.e(TAG, " pToken : " + pToken);
            Log.e(TAG, " dToken : " + dToken);
            Log.e(TAG, " pacienteUID : " + pacienteUID);
            Log.e(TAG, " customer_location : " + customer_location.latitude + " , " + customer_location.longitude);


            Intent resultIntent = new Intent(this, DoctorBooking.class);

            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtra("lat", customer_location.latitude);
            resultIntent.putExtra("lng", customer_location.longitude);
            resultIntent.putExtra("tokenPaciente", pToken);
            resultIntent.putExtra("tokenDoctor", pToken);
            resultIntent.putExtra("pacienteUID", pacienteUID);
            startActivity(resultIntent);
            Log.e(TAG, "============================FIN============================");
        } else if (title.equalsIgnoreCase("DoctorFin")) {
            Log.e(TAG, "========================================================");
            Log.e(TAG, "                        DoctorFin                     ");
            Intent intent = new Intent(this, DoctorHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.e(TAG, "============================FIN============================");
        }


    }





}

