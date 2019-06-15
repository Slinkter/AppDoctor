package com.cudpast.app.doctor.doctorApp.Common;

import android.location.Location;

import com.cudpast.app.doctor.doctorApp.Model.UserPaciente;
import com.cudpast.app.doctor.doctorApp.Model.Usuario;
import com.cudpast.app.doctor.doctorApp.Remote.FCMClient;
import com.cudpast.app.doctor.doctorApp.Remote.IFCMService;
import com.cudpast.app.doctor.doctorApp.Remote.IGoogleAPI;
import com.cudpast.app.doctor.doctorApp.Remote.RetrofitClient;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;

public class Common {

    public static final String TB_AVAILABLE_DOCTOR = "TB_AVAILABLE_DOCTOR";
    public static final String TB_INFO_DOCTOR = "tb_Info_Doctor";
    public static final String TB_INFO_PLASMA = "tb_Info_Plasma";
    public static final String TB_INFO_PACIENTE = "tb_Info_Paciente";
    public static final String TB_SERVICIO_DOCTOR_PACIENTE = "TB_SERVICIO_DOCTOR_PACIENTE";
    public static final String token_tbl = "Tokens";

    public static final String AppPaciente_history = "AppPaciente_history";
    public static final String AppDoctor_history = "AppDoctor_history";


    public static final String db_session = "db_session";



    public static String token_doctor;
    public static Usuario currentUser;
    public static UserPaciente currentPaciente;
    public static Location mLastLocation;

    public static MaterialAnimatedSwitch location_switch;


    private static final String mapURL = "https://maps.googleapis.com";
    private static final String fcmURL = "https://fcm.googleapis.com/";

    public static IGoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(mapURL).create(IGoogleAPI.class);
    }

    public static IFCMService getIFCMService() {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }


    public static double base_fare = 2.5;
    private static double time_rate = 0.35;
    private static double distance_rate = 1.75;

    public static double formulaPrecio(double km, double min) {
        return base_fare + (distance_rate * km) + (time_rate * min);
    }


}
