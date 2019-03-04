package com.cudpast.app.doctor.doctorApp.Common;

import android.location.Location;

import com.cudpast.app.doctor.doctorApp.Model.User;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.Remote.FCMClient;
import com.cudpast.app.doctor.doctorApp.Remote.IFCMService;
import com.cudpast.app.doctor.doctorApp.Remote.IGoogleAPI;
import com.cudpast.app.doctor.doctorApp.Remote.RetrofitClient;

public class Common {

    public static final String TB_AVAILABLE_DOCTOR = "TB_AVAILABLE_DOCTOR";
    public static final String TB_INFO_DOCTOR = "tb_Info_Doctor";
    public static final String TB_INFO_PACIENTE = "tb_Info_Paciente";
    public static final String TB_SERVICIO_DOCTOR_PACIENTE = "TB_SERVICIO_DOCTOR_PACIENTE";
    public static final String token_tbl = "Tokens";


    public static String token_doctor ;
    public static Usuario currentUser;
    public static Location mLastLocation;

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";


    public static double base_fare = 2.5;
    private static double time_rate = 0.35;
    private static double distance_rate = 1.75;

    public  static double formulaPrecio(double km ,double min){
        return base_fare + (distance_rate*km ) + (time_rate*min);
    }





    public static IGoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getIFCMService() {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
