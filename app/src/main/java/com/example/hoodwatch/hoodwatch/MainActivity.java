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
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.github.yuweiguocn.lib.squareloading.SquareLoading;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    GoogleApiClient googleApiClient = null;

    private DatabaseReference mPostReference;
    int maxSize =0;
    ArrayList<Geofence> geoList = new ArrayList<>();
    ArrayList<Flare> listofFlares = new ArrayList<>();
    LocationManager lm;
    Location location;
    SquareLoading sl;
    boolean permissionsAllowed = false;
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
    FloatingActionButton myFab ;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.rv_main);
        rv.setHasFixedSize(false);
        sl = (SquareLoading) findViewById(R.id.SquareLoading);

        mPostReference = FirebaseDatabase.getInstance().getReference();
        adapter = new flareAdapter(this, listofFlares);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                System.out.println(viewHolder.getAdapterPosition()+" : " + adapter.getItemCount());
                listofFlares.get(viewHolder.getAdapterPosition()).setHideFrom("norman");
                Flare flare = listofFlares.get(viewHolder.getAdapterPosition());
                mPostReference.child(flare.getFlareID()).child("hideFrom").push().setValue("norman");
//                mPostReference.child(listofFlares.get(viewHolder.getAdapterPosition()).getFlareID()).child("hideFrom").setValue(listofFlares.get(viewHolder.getAdapterPosition()).getHideFrom());
                changelist();

                adapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallBack);
        itemTouchHelper.attachToRecyclerView(rv);
        final SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                refreshItems();
            }
            void refreshItems() {
                // Load items
                // ...
                listofFlares.clear();
                changelist();
                // Load complete
                onItemsLoadComplete();
            }
            void onItemsLoadComplete() {
                // Update the adapter and notify data set changed
                // ...
                adapter.notifyDataSetChanged();
                // Stop refresh animation
                swipeRefresh.setRefreshing(false);
            }
        });
        myFab = (FloatingActionButton)findViewById(R.id.addFlare);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        geoList.clear();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "connected to Google api client ");
                        boolean check1;
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
                                check1 = true;
                            }else{
                                permissionsAllowed=true;
                                check1 =false;
                            }
                            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
                                check1 = true;
                            }else{
                                check1 =false;
                            }

                            if(!check1){
                                return;
                            }
                            startLocationMonitoring();
                        }
                        ValueEventListener pl = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                allFlares.clear();
                                listofFlares.clear();
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    maxSize = (int)child.getChildrenCount();
//                                    for(DataSnapshot children: child.getChildren()){
                                        Flare flare = new Flare();
                                        flare.setFlareID(child.getKey());
                                        flare.setflareText(child.child("flareText").getValue().toString());
                                        flare.setImagename(child.child("imagename").getValue().toString());
                                        flare.setAddress(child.child("address").getValue().toString());
                                        flare.setTime((Long) child.child("time").getValue());
                                        flare.setType(child.child("type").getValue().toString());
                                        flare.setLatitude((Double) child.child("latitude").getValue());
                                        flare.setLongtitude((Double) child.child("longtitude").getValue());
                                        flare.setUserName(child.child("userName").getValue().toString());
                                        for(DataSnapshot children : child.child("hideFrom").getChildren()){
                                                Log.d("hide",children.getValue().toString());
                                                flare.setHideName(children.getValue().toString());
                                       }

                                        allFlares.add(flare);
                                        geoList.add(new Geofence.Builder()
                                                .setRequestId(flare.getFlareID())
                                                .setCircularRegion(flare.getLatitude(), flare.getLongtitude(), 100)
                                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT| Geofence.GEOFENCE_TRANSITION_DWELL)
                                                .setLoiteringDelay(1000)
                                                .setNotificationResponsiveness(1000)
                                                .build());

//                                    }

                                }
                                Collections.sort(allFlares, new Comparator<Flare>() {
                                    @Override
                                    public int compare(Flare flare, Flare t1) {
                                        return flare.getTime() > t1.getTime() ? -1: flare.getTime() < t1.getTime()  ? 1:0 ;
                                    }
                                });
                                myFab.setOnClickListener((new View.OnClickListener(){
                                    public void onClick(View v){
                                        //call activity add flare
                                        //adapter.notifyDataSetChanged();
                                        Intent intent = new Intent(MainActivity.this,CreateFlareMain.class);
                                        intent.putExtra("username","norman");
                                        intent.putExtra("map", allFlares);
                                        startActivity(intent);
                                        finish();
                                    }
                                }));

                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                    if (location != null) {
                                        lm = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);
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
                                        if(maxSize > 0) {
                                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationlistener);
                                            GeofencingRequest gfRequest = new GeofencingRequest.Builder()
                                                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofences(geoList)
                                                    .build();
                                            Intent intent = new Intent(MainActivity.this, GeofenceService.class);
                                            PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            if (!googleApiClient.isConnected()) {
                                                Log.d(TAG, "google api Client is not connected");
                                            } else {
                                                boolean check = false;
                                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                    // TODO: Consider calling
                                                    //    ActivityCompat#requestPermissions
                                                    // here to request the missing permissions, and then overriding
                                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                    //                                          int[] grantResults)
                                                    // to handle the case where the user grants the permission. See the documentation
                                                    // for ActivityCompat#requestPermissions for more details.
                                                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                                                        check = true;
                                                    } else {
                                                        check = false;
                                                    }
                                                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                                                        check = true;
                                                    } else {
                                                        check = false;
                                                    }

                                                    if (check == false) {
                                                        Log.d("geofence", "failed to allow geofence");
                                                    }

                                                }
                                                LocationServices.GeofencingApi.addGeofences(googleApiClient, gfRequest, pendingIntent).setResultCallback(new ResultCallback<Status>() {
                                                    @Override
                                                    public void onResult(@NonNull Status status) {
                                                        if (status.isSuccess()) {
                                                            Log.d(TAG, "successfully added geofence");

                                                        } else {
                                                            Log.d(TAG, "failed to add geofence " + status.getStatus());
                                                        }
                                                    }
                                                });
                                            }
                                            changelist();
                                            adapter.notifyDataSetChanged();
                                            if(sl.VISIBLE != View.GONE){
                                                sl.setVisibility(View.GONE);
                                            }

                                        }
                                    }
                                }

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
    private void startLocationMonitoring() {
        Log.d("geo", "startLocation monitoring");
        LocationRequest lr = LocationRequest.create().setInterval(10000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        else {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, lr, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location1) {
                    location.setLatitude(location1.getLatitude());
                    location.setLongitude(location1.getLongitude());
                }
            });
        }


    }
    private void changelist(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationMonitoring();
            if(listofFlares.size() > 0){
                listofFlares.clear();
            }
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Location locationA = new Location("point A");
            locationA.setLongitude(longitude);
            locationA.setLatitude(latitude);
            Location locationB = new Location("point B");
            for (Flare flare : allFlares) {
                locationB.setLatitude(flare.getLatitude());
                locationB.setLongitude(flare.getLongtitude());
                double distance = location.distanceTo(locationB);
                if (distance < 5000 && !flare.getHideArray().contains(flare.getUserName())) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    df.setRoundingMode(RoundingMode.CEILING);
                    flare.setFlareDistance(Double.parseDouble(df.format(distance)));
                    listofFlares.add(flare);

                }

            }
            adapter.notifyItemInserted(0);
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