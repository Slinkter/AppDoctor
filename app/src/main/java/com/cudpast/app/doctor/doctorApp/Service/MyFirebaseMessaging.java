package com.cudpast.app.doctor.doctorApp.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.cudpast.app.doctor.doctorApp.Activities.MainActivity;
import com.cudpast.app.doctor.doctorApp.Business.DoctorBooking;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    //son dos casos
    //1.El usuario enviar una solicitud de atencion
    //2.El usuario cancela en cualquier momento la solicutud de atencion
    //primer plano  Notificacion
    //segundo plano Data
    public static final String TAG = MyFirebaseMessaging.class.getSimpleName();
    private static final String CHANNEL_ID = "MyMessagin";
    private static final int NOTIFICATION_ID = 9;


    public static final String APP_CHANNEL_ID = "Default";
    public static final String APP_CHANNEL_NAME = "App Channel";


    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "                 MyFirebaseMessaging                    ");
        Log.e(TAG, "" + remoteMessage.getData().get("title"));
        Log.e(TAG, "" + remoteMessage.getData().get("body"));
        Log.e(TAG, "" + remoteMessage.getData().get("pToken"));
        Log.e(TAG, "" + remoteMessage.getData().get("dToken"));
        Log.e(TAG, "" + remoteMessage.getData().get("json_lat_log"));
        Log.e(TAG, "" + remoteMessage.getData().get("pacienteUID"));

        //.Booking
        if ((remoteMessage.getData().get("body")).equalsIgnoreCase("Usted tiene una solicutud de atención")) {
            doctorBooking(remoteMessage);
        } else if ((remoteMessage.getData().get("body")).equalsIgnoreCase("El usuario ha cancelado")) {
            doctorCanceled();
        } else if ((remoteMessage.getData().get("body")).equalsIgnoreCase("El usuario ha finalizado")) {
            doctorUserEnded();
        }
    }


    private void doctorBooking(RemoteMessage remoteMessage) {

        Log.e(TAG, "========================================================");
        Log.e(TAG, "       Atención Medica             ");

        LatLng customer_location = new Gson().fromJson(remoteMessage.getData().get("json_lat_log"), LatLng.class);
        //
        Intent intent = new Intent(this, DoctorBooking.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("title", remoteMessage.getData().get("title"));
        intent.putExtra("body", remoteMessage.getData().get("body"));
        intent.putExtra("pToken", remoteMessage.getData().get("pToken"));
        intent.putExtra("dToken", remoteMessage.getData().get("dToken"));
        intent.putExtra("lat", customer_location.latitude);
        intent.putExtra("lng", customer_location.longitude);
        intent.putExtra("pacienteUID", remoteMessage.getData().get("pacienteUID"));
        //
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //
        Notification notification = new Notification.Builder(this).setContentText("hola").build() ;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, APP_CHANNEL_ID);
        builder
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setSmallIcon(R.drawable.ic_local_hospital_black)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ambulance))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColorized(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel appChannel = new NotificationChannel(APP_CHANNEL_ID, APP_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            appChannel.setDescription(remoteMessage.getData().get("body"));
            appChannel.enableLights(true);

            appChannel.setLightColor(Color.GREEN);
            appChannel.enableVibration(true);

            appChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

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
        nmc.notify(123, builder.build());
        Log.e(TAG, "============================FIN============================");
    }

    private void doctorCanceled() {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "        El usuario ha cancelado             ");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Log.e(TAG, "============================FIN============================");
    }

    private void doctorUserEnded() {
        Log.e(TAG, "========================================================");
        Log.e(TAG, "                        DoctorEnd                     ");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Log.e(TAG, "============================FIN============================");
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



        /*
        Log.e(TAG, "========================================================");
        Log.e(TAG, "                 MyFirebaseMessaging                    ");
        final String title = remoteMessage.getNotification().getTitle();
        final String body = remoteMessage.getNotification().getBody();

        if (title.equalsIgnoreCase("el usuario ha cancelado")) {
            Log.e(TAG, "        El usuario ha cancelado             ");
            Intent intent = new Intent(this, MainActivity.class);
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
        } else if (title.equalsIgnoreCase("DoctorEnd")) {
            Log.e(TAG, "========================================================");
            Log.e(TAG, "                        DoctorEnd                     ");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.e(TAG, "============================FIN============================");
        }

       */

}

