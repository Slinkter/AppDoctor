package com.cudpast.app.doctor.doctorApp.Business;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class TripDetail extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView txtDate, txtFee, txtBaseFare, txtTime, txtDistance, txtEstimatedPayout, txtFrom, txtTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Init
        txtBaseFare = findViewById(R.id.textBaseFare);
        txtDate = findViewById(R.id.txtDate);
        txtFee = findViewById(R.id.txtFee);
        txtTime = findViewById(R.id.textTiempo);
        txtDistance = findViewById(R.id.textDistancia);
        txtEstimatedPayout = findViewById(R.id.txtEstimatedPayout);
        txtFrom = findViewById(R.id.txtFrom);
        txtTo = findViewById(R.id.txtTo);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        settingInformation();
    }

    private void settingInformation() {

        if (getIntent() != null) {
            Calendar calendar = Calendar.getInstance();

            String date = String.format("%s,%d/%d", convertToDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)), calendar.get(calendar.DAY_OF_MONTH), calendar.get(calendar.MONTH));
            txtDate.setText(date);

            txtFee.setText(String.format(" s/ %.2f", getIntent().getDoubleExtra("total", 0.0f)));
            txtEstimatedPayout.setText(String.format(" s/ %.2f", getIntent().getDoubleExtra("total", 0.0f)));
            txtBaseFare.setText(String.format(" s/ %.2f", Common.base_fare));

            //


            txtFrom.setText(getIntent().getStringExtra("start_address"));
            txtTo.setText(getIntent().getStringExtra("end_address"));

            //

            txtTime.setText(String.format("%s min", getIntent().getStringExtra("time")));
            txtDistance.setText(String.format(" %s km", getIntent().getStringExtra("distance")));
            //
            Log.e("TRIPDETAIL", "------->" + getIntent().getStringExtra("time"));
            Log.e("TRIPDETAIL", "------->" + getIntent().getStringExtra("distance"));
            //

            String[] location_end = getIntent().getStringExtra("location_end").split(",");
            LatLng dropOff = new LatLng(Double.parseDouble(location_end[0]), Double.parseDouble(location_end[1]));

            mMap.addMarker(new MarkerOptions()
                    .position(dropOff)
                    .title("Drop off here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            );

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dropOff, 15.0f));


        }
    }

    private String convertToDayOfWeek(int day) {
        switch (day) {

            case Calendar.MONDAY:
                return "Lunes";
            case Calendar.TUESDAY:
                return "Martes";
            case Calendar.WEDNESDAY:
                return "Miercoles";
            case Calendar.THURSDAY:
                return "Jueves";
            case Calendar.FRIDAY:
                return "Viernes";
            case Calendar.SATURDAY:
                return "Sabado";
            case Calendar.SUNDAY:
                return "Domingo";
            default:
                return "404 DIA";
        }


    }
}
