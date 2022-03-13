package com.flom.mobilecomputingproject;

import static android.os.Process.SIGNAL_KILL;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.flom.mobilecomputingproject.database.DatabaseManager;
import com.flom.mobilecomputingproject.model.Balise;
import com.flom.mobilecomputingproject.model.ReminderEnum;
import com.flom.mobilecomputingproject.notifications.NotificationWorker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;

    private SharedPreferences preferences;
    private DatabaseManager myDB;

    private ReminderEnum[] reminderEnums;


    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public List<Balise> tableauDesBalises, tableauDesBalisesTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        myDB = new DatabaseManager(MapsActivity.this);

        reminderEnums = ReminderEnum.values();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        tableauDesBalises = new ArrayList<>();
        tableauDesBalisesTemp = new ArrayList<>();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void activerGPSWindow() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission. ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng start = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 17));
                        }
                    }
                });


        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Services de localisation inactifs");
            builder.setMessage("Veuillez activer les services de localisation et le GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        googleMap.setMyLocationEnabled(true);



        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                for (Balise balisetemp : tableauDesBalises) {
                    balisetemp.supprimerMarqueur();
                }
                tableauDesBalises.clear();
                for (Balise balisetemp : tableauDesBalisesTemp) {
                    balisetemp.supprimerMarqueur();
                }
                tableauDesBalisesTemp.clear();

                Balise baliseMarqueur = new Balise(point, "Votre marqueur", "");
                tableauDesBalisesTemp.add(baliseMarqueur);
                baliseMarqueur.creerMarqueurTemp(googleMap);

                Location startPoint = new Location("startPoint");
                startPoint.setLatitude(point.latitude);
                startPoint.setLongitude(point.longitude);

                Cursor cursor = myDB.readAllReminders(reminderEnums[preferences.getInt("ReminderDisplayMode", 0)].toString(), preferences.getString("ReminderDisplayOrder", "ASC"), true);

                while (cursor.moveToNext()) {
                    Location endPoint = new Location("endPoint");
                    endPoint.setLatitude(cursor.getDouble(6));
                    endPoint.setLongitude(cursor.getDouble(7));

                    double distance = startPoint.distanceTo(endPoint);

                    if (cursor.getDouble(6) != -1 && cursor.getDouble(7) != -1 && distance < 5000) {

                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date date = null;
                        try {
                            if (!cursor.getString(3).isEmpty()) date = fmt.parse(cursor.getString(3));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat fmtOut = new SimpleDateFormat("EEEE dd LLLL yyyy - HH:mm", Locale.getDefault());
                        String reminder_time;
                        if (date != null) reminder_time = fmtOut.format(date);
                        else reminder_time = "";


                        LatLng latLng = new LatLng(cursor.getDouble(6), cursor.getDouble(7));
                        Balise balise = new Balise(latLng, cursor.getString(1), reminder_time);
                        tableauDesBalises.add(balise);
                    }
                }

                for (Balise tabBalise : tableauDesBalises) {
                    tabBalise.creerMarqueur(googleMap);
                }
            }
        });


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location startPoint = new Location("startPoint");
                startPoint.setLatitude(locationResult.getLastLocation().getLatitude());
                startPoint.setLongitude(locationResult.getLastLocation().getLongitude());

                Cursor cursor = myDB.readAllReminders(reminderEnums[preferences.getInt("ReminderDisplayMode", 0)].toString(), preferences.getString("ReminderDisplayOrder", "ASC"), true);

                while (cursor.moveToNext()) {
                    Location endPoint = new Location("endPoint");
                    endPoint.setLatitude(cursor.getDouble(6));
                    endPoint.setLongitude(cursor.getDouble(7));

                    double distance = startPoint.distanceTo(endPoint);

                    if (distance < 10 && cursor.getString(5).equals("false")) {
                        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                                .setInputData(new Data.Builder()
                                        .putString("IMAGE_URI", cursor.getString(2))
                                        .putInt("ID", cursor.getInt(0))
                                        .putBoolean("SHOW_NOTIFICATION", true)
                                        .build())
                                .addTag(cursor.getString(1)).build();

                        WorkManager.getInstance().enqueue(oneTimeWorkRequest);
                    }
                }
            }
        }, Looper.getMainLooper());
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Localisation refusé")
                            .setMessage("L'application a besoin de la localisation de l'appareil pour fonctionné correctement")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Process.sendSignal(Process.myPid(), SIGNAL_KILL);
                                }
                            })
                            .create()
                            .show();
                }
                return;
            }
        }
    }
}