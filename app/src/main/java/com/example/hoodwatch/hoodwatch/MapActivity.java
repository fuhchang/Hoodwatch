package com.example.hoodwatch.hoodwatch;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    double rplat = 1.44309, rplng = 103.785581;
    double hmlat = 1.370206, hmlng = 103.8344523;
    double nyplat = 1.3773754;
    double nyplng = 103.8485727;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(nyplat, nyplng), 20));
        map.addMarker(new MarkerOptions().position(new LatLng(nyplat, nyplng)));
        CircleOptions circleOptions = new CircleOptions()
                .center( new LatLng(nyplat, nyplng) )
                .radius(100)
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);
        map.addCircle(circleOptions);
        map.addMarker(new MarkerOptions().position(new LatLng(hmlat, hmlng)));
        CircleOptions circleOptionshm = new CircleOptions()
                .center( new LatLng(hmlat, hmlng) )
                .radius(100)
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);
        map.addCircle(circleOptionshm);
        map.addMarker(new MarkerOptions().position(new LatLng(rplat, rplng)));
        CircleOptions circleOptionsrp = new CircleOptions()
                .center( new LatLng(rplat, rplng) )
                .radius(100)
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);
        map.addCircle(circleOptionsrp);

    }
}
