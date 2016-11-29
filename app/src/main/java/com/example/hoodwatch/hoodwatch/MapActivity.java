package com.example.hoodwatch.hoodwatch;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    double lat;
    double lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle bundle = getIntent().getExtras();
        lat = bundle.getDouble("lat");
        lng = bundle.getDouble("long");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("loc", lat+ " " + lng);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lng), 18));
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
        CircleOptions circleOptions = new CircleOptions()
                .center( new LatLng(lat, lng) )
                .radius(100)
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);
        map.addCircle(circleOptions);
    }
}
