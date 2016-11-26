package com.example.hoodwatch.hoodwatch;


import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    GoogleApiClient googleApiClient = null;
    double rplat = 1.4441461;
    double rplng = 103.7835478;
    double hmlat=1.370206;
    double hmlng=103.8344523;
    double nyplat = 1.3773754 ;
    double nyplng = 103.8485727;
    Fragment f;
    ArrayList<Geofence> geoList = new ArrayList<Geofence>();
    @Override
    protected void onResume() {
        super.onResume();
        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d(TAG, "google play not avaliable");
            GoogleApiAvailability.getInstance().getErrorDialog(this, response, 1).show();
        } else {
            Log.d(TAG, "Google Play Service is avaliable");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        f = Fragment.instantiate(this, MapFragment.class.getName());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, f).commit();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "connected to Google api client ");
                        boolean check = false;
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            System.out.println("hello world");
                            if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
                                check = true;
                            }else{
                                check =false;
                            }
                            if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},0);
                                check = true;
                            }else{
                                check =false;
                            }

                            if(check==false){
                                return;
                            }

                        }


                        startLocationMonitoring();
                        startGeofenceMonitoring();


                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "suspended connected to Google api client ");
                    }
                }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "fail tp connect to Google api client - " + connectionResult.getErrorMessage());
                    }
                }).build();



    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.reconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private void startLocationMonitoring() {
        Log.d(TAG, "startLocation monitoring");
        LocationRequest lr = LocationRequest.create().setInterval(10000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, lr, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "lOCATION UPDATE LAT: " + location.getLatitude() + " LNG: " + location.getLongitude());
            }
        });


    }

    public void startGeofenceMonitoring() {

        geoList.add(new Geofence.Builder()
                .setRequestId("nyp")
                .setCircularRegion(nyplat, nyplng, 100)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT| Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(0)
                .setNotificationResponsiveness(0)
                .build());
        geoList.add(new Geofence.Builder()
                .setRequestId("hm")
                .setCircularRegion(hmlat, hmlng, 100)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT| Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(0)
                .setNotificationResponsiveness(0)
                .build());
        geoList.add(new Geofence.Builder()
                .setRequestId("rp")
                .setCircularRegion(rplat, rplng, 100)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT| Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(0)
                .setNotificationResponsiveness(0)
                .build());

        GeofencingRequest gfRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofences(geoList)
                .build();
        Intent intent = new Intent(this, GeofenceService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "google api Client is not connected");
        } else {
            boolean check = false;
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                System.out.println("hello world");
                if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
                    check = true;
                }else{
                    check =false;
                }
                if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},0);
                    check = true;
                }else{
                    check =false;
                }

                if(check==false){
                    Log.d("geofence", "failed to allow geofence");
                    return;
                }

            }
            LocationServices.GeofencingApi.addGeofences(googleApiClient, gfRequest, pendingIntent).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if(status.isSuccess()){
                        Log.d(TAG,"successfully added geofence");

                    }else{
                        Log.d(TAG,"failed to add geofence " + status.getStatus());
                    }
                }
            });
        }
    }

    private void stopGeofenceMonitoring(){
        Log.d(TAG, "stop monitoring called");
        ArrayList<String> geofenceIds = new ArrayList<String>();
        geofenceIds.add("test");
        LocationServices.GeofencingApi.removeGeofences(googleApiClient,geofenceIds);
    }
}
