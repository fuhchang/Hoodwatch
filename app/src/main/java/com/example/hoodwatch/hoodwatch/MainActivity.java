package com.example.hoodwatch.hoodwatch;



import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    GoogleApiClient googleApiClient = null;
    private DatabaseReference mPostReference;
    int maxSize =0;
    ArrayList<Geofence> geoList = new ArrayList<>();
    ArrayList<Flare> listofFlares = new ArrayList<>();
    LocationManager lm;
    Location location;
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
    ArrayList<Flare> allFlares = new ArrayList<>();
    private flareAdapter adapter;
    private RecyclerView rv;
    LinearLayout ll;
    int maxcount=0;
    String path = "data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir";
    FloatingActionButton myFab ;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.rv_main);
        rv.setHasFixedSize(true);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        adapter = new flareAdapter(this, listofFlares);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
        myFab = (FloatingActionButton)findViewById(R.id.addFlare);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "connected to Google api client ");
                        boolean check;
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
                                check = true;
                            }else{
                                check =false;
                            }
                            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
                                check = true;
                            }else{
                                check =false;
                            }

                            if(!check){
                                return;
                            }

                        }
                        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                        if (location == null) {
                            System.out.println("HAHAHA SHIT ASSIGNMENT");
                        }
                        lm = (LocationManager)getSystemService(MainActivity.LOCATION_SERVICE);
                        android.location.LocationListener locationlistener = new android.location.LocationListener() {
                            @Override
                            public void onLocationChanged(Location location1) {
                                location = location1;
                            }

                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {

                            }

                            @Override
                            public void onProviderEnabled(String s) {

                            }

                            @Override
                            public void onProviderDisabled(String s) {

                            }
                        };

                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationlistener);
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference myRef = database.child("event/");

                        ValueEventListener pl = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                allFlares.clear();
                                listofFlares.clear();
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    maxSize = (int)child.getChildrenCount();
                                    System.out.println(maxSize);
                                    for(DataSnapshot children: child.getChildren()){
                                       Flare flare = children.getValue(Flare.class);

                                        allFlares.add(flare);
                                    }

                                }
                                myFab.setOnClickListener((new View.OnClickListener(){
                                    public void onClick(View v){
                                        //call activity add flare
                                        //adapter.notifyDataSetChanged();
                                        Intent intent = new Intent(MainActivity.this,CreateFlareType.class);
                                        intent.putExtra("username","norman");

                                        intent.putExtra("maxcount", maxSize);

                                        intent.putExtra("map", allFlares);
                                        startActivity(intent);
                                        finish();
                                    }
                                }));
                                System.out.println("llkskdskd");
                                changelist();

                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        mPostReference.addValueEventListener(pl);
//                        mPostReference.addValueEventListener(postListener);
//                        maxcount = loadCSV();


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
    private void changelist(){
        Geocoder geocoder = new Geocoder(getBaseContext());
        geoList.clear();
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int permissionCheck = MainActivity.this.checkCallingOrSelfPermission(permission);
        Geocoder geocode = new Geocoder(getBaseContext());
        //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            System.out.println("ppp");
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Location locationA = new Location("point A");
            locationA.setLongitude(longitude);
            locationA.setLatitude(latitude);
            Location locationB = new Location("point B");
            for (Flare flare : allFlares) {
                locationB.setLatitude(flare.getLatitude());
                locationB.setLongitude(flare.getLongtitude());
                float distance = locationA.distanceTo(locationB);
                if (distance < 500) {
                    listofFlares.add(flare);
                    geoList.add(new Geofence.Builder()
                            .setRequestId(flare.getFlareID())
                            .setCircularRegion(flare.getLatitude(), flare.getLongtitude(), 100)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT| Geofence.GEOFENCE_TRANSITION_DWELL)
                            .setLoiteringDelay(1000)
                            .setNotificationResponsiveness(1000)
                            .build());
                }
            }
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
    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    /*
    private int loadCSV(){
        BufferedReader reader = null;
        Geocoder geocoder = new Geocoder(getBaseContext());
        try {

            reader = new BufferedReader(new FileReader(new File("/data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir/flares.csv")));
            String csvLine;
            int count=0;
            ArrayList<String[]> allRows = new ArrayList<>();
            while ((csvLine = reader.readLine()) != null) {

                String[] row = csvLine.split(",");
                allRows.add(count,row);
                count++;
            }
            reader.close();
            allFlares.clear();
            geoList.clear();
            Log.d("count: ", String.valueOf(count));
            for(int i=0 ; i < count; i++){
                String[] row = allRows.get(count-(i+1));
                Flare f = new Flare();
                f.setFlareID(row[0]);
                f.setImagename(row[1]);
                f.setFlareText(row[2]);
                f.setClassification(row[3]);
                f.setUserName(row[4]);
                f.setLatitude(Double.parseDouble(row[5]));
                f.setLongtitude(Double.parseDouble(row[6]));
                f.setTime(Long.parseLong(row[7]));
                List<android.location.Address> list = geocoder.getFromLocation(f.getLatitude(),f.getLongtitude(),3);
                f.setAddress(String.valueOf(list.get(0).getAddressLine(0)));
                allFlares.add(f);
                geoList.add(new Geofence.Builder()
                        .setRequestId(allFlares.get(i).getFlareID())
                        .setCircularRegion(allFlares.get(i).getLatitude(), allFlares.get(i).getLongtitude(), 100)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT| Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setLoiteringDelay(1000)
                        .setNotificationResponsiveness(1000)
                        .build());
            }
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
            return count;
        }
        catch (Exception e) {

        }
        return 0;
    }
*/
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

}