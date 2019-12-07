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
import android.util.Log;

import com.cudpast.app.doctor.doctorApp.Business.DoctorBooking;
import com.cudpast.app.doctor.doctorApp.Business.DoctorCancel;
import com.cudpast.app.doctor.doctorApp.Business.DoctorCancelOnRoad;
import com.cudpast.app.doctor.doctorApp.Business.DoctorFinish;
import com.cudpast.app.doctor.doctorApp.Business.Cancel.DoctorTimeOut;
import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.R;
import com.cudpast.app.doctor.doctorApp.Soporte.Token;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    //son dos casos
    //1.El usuario enviar una solicitud de atencion
    //2.El usuario cancela en cualquier momento la solicutud de atencion

    public static final String TAG = MyFirebaseMessaging.class.getSimpleName();
    public static final String APP_CHANNEL_ID = "Default";
    public static final String APP_CHANNEL_NAME = "App Channel";

    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "                 MyFirebaseMessaging                    ");
        //
        Log.e(TAG, "title : " + remoteMessage.getData().get("title"));
        Log.e(TAG, "body : " + remoteMessage.getData().get("body"));
        Log.e(TAG, "pToken : " + remoteMessage.getData().get("pToken"));
        Log.e(TAG, "dToken : " + remoteMessage.getData().get("dToken"));
        Log.e(TAG, "json_lat_log : " + remoteMessage.getData().get("json_lat_log"));
        Log.e(TAG, "pacienteUID : " + remoteMessage.getData().get("pacienteUID"));
        //Casos -- Funciona con data
        String caso_1 = "Usted tiene una solicutud de atención";
        String caso_2 = "El usuario ha cancelado";
        String caso_3 = "Tiempo fuera";
        String caso_4 = "El usuario ha cancelado durante el servicio";
        String caso_5 = "El usuario ha finalizado";
        //.
        String body = remoteMessage.getData().get("body");
        //.
        if ((body).equalsIgnoreCase(caso_1)) {
            doctorBooking(remoteMessage);
        } else if ((body).equalsIgnoreCase(caso_2)) {
            pacienteConsultaCanceled(remoteMessage);
        } else if ((body).equalsIgnoreCase(caso_3)) {
            timeOutRequestDoctor(remoteMessage);
        } else if ((body).equalsIgnoreCase(caso_4)) {
            doctorCanceledOnRoad();
        } else if ((body).equalsIgnoreCase(caso_5)) {
            doctorUserEnded();
        }
        //
    }

    private void doctorBooking(RemoteMessage message) {
        Log.e(TAG, "========================================================");
        Log.e(TAG, " Caso 1 : ");
        LatLng customer_location = new Gson().fromJson(message.getData().get("json_lat_log"), LatLng.class);
        //
        Intent intent = new Intent(this, DoctorBooking.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //
        intent.putExtra("title", message.getData().get("title"));
        intent.putExtra("body", message.getData().get("body"));
        intent.putExtra("pToken", message.getData().get("pToken"));
        intent.putExtra("dToken", message.getData().get("dToken"));
        intent.putExtra("paciente_lat", customer_location.latitude);
        intent.putExtra("paciente_lng", customer_location.longitude);
        intent.putExtra("pacienteUID", message.getData().get("pacienteUID"));
        //
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, APP_CHANNEL_ID);
        builder
                .setContentTitle(message.getData().get("title"))
                .setContentText(message.getData().get("body"))
                .setSmallIcon(R.drawable.ic_local_hospital_black)//icono de la notificacion
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ambulance)) // imagen del mensaje
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColorized(true)
                .setContentIntent(pendingIntent);
        //
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel appChannel = new NotificationChannel(APP_CHANNEL_ID, APP_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            appChannel.setDescription(message.getData().get("body"));
            appChannel.setLightColor(Color.GREEN);
            appChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            appChannel.enableLights(true);
            appChannel.enableVibration(true);
            notificationManager.createNotificationChannel(appChannel);
        } else {
            int color = getResources().getColor(R.color.colorRed);
            builder
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{100, 250})
                    .setColor(color)
                    .setLights(Color.YELLOW, 500, 5000);
        }
        NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
        //todo : aca debe ir una especie de ID del usuario para que cancele en la anterior notificaciòn
        nmc.notify(123, builder.build());
        Log.e(TAG, "============================FIN============================");
    }

    private void pacienteConsultaCanceled(RemoteMessage message) {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "        El paciente ha cancelado  la consulta           ");
        Intent intent = new Intent(this, DoctorCancel.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, APP_CHANNEL_ID);
        builder
                .setContentTitle(message.getData().get("title"))
                .setContentText(message.getData().get("body"))
                .setSmallIcon(R.drawable.ic_local_hospital_black)//icono de la notificacion
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ambulance)) // imagen del mensaje
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColorized(true)
                .setContentIntent(pendingIntent);
        //
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel appChannel = new NotificationChannel(APP_CHANNEL_ID, APP_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            appChannel.setDescription(message.getData().get("body"));
            appChannel.setLightColor(Color.GREEN);
            appChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            appChannel.enableLights(true);
            appChannel.enableVibration(true);
            notificationManager.createNotificationChannel(appChannel);
        } else {
            int color = getResources().getColor(R.color.colorRed);
            builder
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{100, 250})
                    .setColor(color)
                    .setLights(Color.YELLOW, 500, 5000);
        }
        NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
        //todo : aca debe ir una especie de ID del usuario para que cancele en la anterior notificación
        nmc.notify(123, builder.build());

        Log.e(TAG, "============================FIN============================");
    }

    private void doctorCanceledOnRoad() {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "       El paciente ha cancelado durante el servicio            ");
        Intent intent = new Intent(this, DoctorCancelOnRoad.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Log.e(TAG, "============================FIN============================");
    }

    private void timeOutRequestDoctor(RemoteMessage message) {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "       Tiempo fuera           ");
        Intent intent = new Intent(this, DoctorTimeOut.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //
        //
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, APP_CHANNEL_ID);
        builder
                .setContentTitle(message.getData().get("title"))
                .setContentText(message.getData().get("body"))
                .setSmallIcon(R.drawable.ic_local_hospital_black)//icono de la notificacion
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ambulance)) // imagen del mensaje
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColorized(true)
                .setContentIntent(pendingIntent);
        //
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel appChannel = new NotificationChannel(APP_CHANNEL_ID, APP_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            appChannel.setDescription(message.getData().get("body"));
            appChannel.setLightColor(Color.GREEN);
            appChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            appChannel.enableLights(true);
            appChannel.enableVibration(true);
            notificationManager.createNotificationChannel(appChannel);
        } else {
            int color = getResources().getColor(R.color.colorRed);
            builder
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{100, 250})
                    .setColor(color)
                    .setLights(Color.YELLOW, 500, 5000);
        }
        NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
        //todo : aca debe ir una especie de ID del usuario para que cancele en la anterior notificación
        nmc.notify(123, builder.build());

        Log.e(TAG, "============================FIN============================");
    }

    private void doctorUserEnded() {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "                        DoctorEnd                     ");
        Intent intent = new Intent(this, DoctorFinish.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Log.e(TAG, "============================FIN============================");
    }


    @Override
    public void onNewToken(String token) {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.e(TAG, "Refreshed token: " + token);
        Log.e(TAG,"Refreshed token: " + refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        updateTokenToServer(token);
    }

    private void updateTokenToServer(String refreshedToken) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tbl);
        Token token = new Token(refreshedToken);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            tokens.child(FirebaseAuth
                    .getInstance()
                    .getCurrentUser()
                    .getUid())
                    .setValue(token);
        }

    }


}
