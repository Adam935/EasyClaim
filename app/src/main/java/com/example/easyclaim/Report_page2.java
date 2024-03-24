package com.example.easyclaim;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.example.easyclaim.util.AppPermission;
import com.example.easyclaim.view.SpinnerHelper;
import com.example.easyclaim.database.DataRecorder;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;

public class Report_page2 extends AppCompatActivity {
    private DataRecorder dataRecorder;
    private FusedLocationProviderClient fusedLocationClient;
    private AppPermission appPermission;

    private LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report2);
        SpinnerHelper.setupSpinners(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        appPermission = new AppPermission(this);
        appPermission.checkAndRequestLocationPermissions();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        EditText exactLocationEditText = findViewById(R.id.exactLocationEditText);
                        exactLocationEditText.setText(location.getLatitude() + ", " + location.getLongitude());
                    }
                }
            }
        };

        if (appPermission.checkLocationPermission()) {
            setExactLocationEditText();
        }

        Button Continu_the_report = findViewById(R.id.continueButton);
        Continu_the_report.setOnClickListener(v -> {
            // Initialize DataRecorder
            dataRecorder = new DataRecorder(this);
            dataRecorder.clearData("report_page2_test.txt");
            // Save the report
            dataRecorder.saveReportPage2(this);
            Intent intent = new Intent(Report_page2.this, Report_page3.class);
            startActivity(intent);
        });
    }

    private void setExactLocationEditText() {
        if (appPermission.checkLocationPermission()) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            Log.d("Report_page2", "Location permissions not granted");
            appPermission.requestLocationPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}