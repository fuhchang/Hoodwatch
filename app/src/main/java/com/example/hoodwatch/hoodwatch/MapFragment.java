package com.example.hoodwatch.hoodwatch;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapFragment extends Fragment{
    private MapView mMap;
    GoogleMap map;
    double rplat= 1.44309, rplng=103.785581;
    double hmlat=1.370206, hmlng=103.8344523;
    ArrayList<Geofence> gList = new ArrayList<Geofence>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View rootView = inflater.inflate(R.layout.activity_map_fragment,
                container, false);

        mMap = (MapView) rootView.findViewById(R.id.googleMapView);
        mMap.onCreate(savedInstanceState);
        mMap.onResume();

        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(	hmlat, hmlng), 100));
                map.addMarker(new MarkerOptions().position(new LatLng(hmlat, hmlng)));
                CircleOptions circleOptions = new CircleOptions()
                        .center( new LatLng(hmlat, hmlng) )
                        .radius(10)
                        .fillColor(0x40ff0000)
                        .strokeColor(Color.TRANSPARENT)
                        .strokeWidth(2);
                map.addCircle(circleOptions);
               // creategeofences(1.290270,103.851959,new String("st andrew"));
            }
        });
        return rootView;
    }

/*
    public void creategeofences(Double lat, Double lng,String id){

        gList.add(new Geofence.Builder().setRequestId(id).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).setCircularRegion(lat, lng,10).setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build());
    }
    */
}
