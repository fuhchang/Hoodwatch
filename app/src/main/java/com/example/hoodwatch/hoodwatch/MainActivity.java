package com.example.hoodwatch.hoodwatch;



import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    GoogleApiClient googleApiClient = null;

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
    ArrayList<Flare> allFlares = new ArrayList<>();
    private flareAdapter adapter;
    private RecyclerView rv;
    LinearLayout ll;
    int maxcount=0;
    String path = "data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir";
    FloatingActionButton myFab ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.rv_main);

        adapter = new flareAdapter(this, allFlares);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rv.setLayoutManager(mLayoutManager);
        rv.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
        myFab = (FloatingActionButton)findViewById(R.id.addFlare);

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
        maxcount = loadCSV();
        myFab.setOnClickListener((new View.OnClickListener(){
            public void onClick(View v){
                //call activity add flare
                //adapter.notifyDataSetChanged();
                Intent intent = new Intent(MainActivity.this,CreateFlareType.class);
                intent.putExtra("username","norman");
                if(maxcount == 0) {
                    intent.putExtra("maxcount", 0);
                }else{
                    intent.putExtra("maxcount", maxcount);
                }
                intent.putExtra("map", allFlares);
                startActivity(intent);
                finish();
            }
        }));
        adapter.notifyDataSetChanged();
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    private int loadCSV(){
        BufferedReader reader = null;
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
            for(int i=0 ; i < count; i++){
                String[] row = allRows.get(i);
                Flare f = new Flare();
                Log.d("csv loading", "reading");
                Log.d("csv loading", row[0]);
                f.setFlareID(row[0]);

                f.setImagename(row[1]);

                f.setFlareText(row[2]);

                f.setClassification(row[3]);

                f.setUserName(row[4]);

                f.setLatitude(Double.parseDouble(row[5]));
                f.setLongtitude(Double.parseDouble(row[6]));
                f.setTime(Long.parseLong(row[7]));
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

    private Bitmap loadImageFromStorage(String path, String imageName)
    {
        Bitmap b = null;
        try {
            File f=new File(path, imageName);
            b = BitmapFactory.decodeStream(new FileInputStream(f));


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
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