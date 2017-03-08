package com.example.hoodwatch.hoodwatch;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.Nullable;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.eminayar.panter.DialogType;
import com.eminayar.panter.PanterDialog;
import com.eminayar.panter.enums.Animation;
import com.eminayar.panter.interfaces.OnSingleCallbackConfirmListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static com.example.hoodwatch.hoodwatch.R.mipmap.flare;

public class LocationSelectionActivity extends FragmentActivity implements OnMapReadyCallback {
    LatLng myLoc;
    GoogleMap mMap;
    double lat;
    double lng;
    String imgName;
    Marker marker;
    Circle circle;
    Flare flare;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);
        Bundle bundle = getIntent().getExtras();
        flare = (Flare) getIntent().getSerializableExtra("flare");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        new PanterDialog(this)
                .setHeaderBackground(R.color.colorPrimary)
                .setHeaderLogo(R.mipmap.ic_launcher)
                .setPositive("I GOT IT")// You can pass also View.OnClickListener as second param
                .setMessage("Click on the map to set Hazard point")
                .isCancelable(false)
                .show();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(flare.getLatitude(),flare.getLongtitude()),18));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(marker != null){
                    marker.remove();
                }
                if(circle != null){
                    circle.remove();
                }
                circle = googleMap.addCircle(new CircleOptions().center(latLng).radius(100));
                marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                        .title("Hazard location"));
                flare.setLatitude(latLng.latitude);
                flare.setLongtitude(latLng.longitude);

                                new PanterDialog(LocationSelectionActivity.this)
                                        .setHeaderBackground(R.color.colorPrimary)
                                        .setHeaderLogo(R.mipmap.ic_launcher)
                                        .setPositive("YES", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                new PanterDialog(LocationSelectionActivity.this)
                                                        .setHeaderBackground(R.color.colorPrimary)
                                                        .setHeaderLogo(R.mipmap.ic_launcher)
                                                        .setDialogType(DialogType.SINGLECHOICE)
                                                        .isCancelable(false).withAnimation(Animation.POP)
                                                        .items(R.array.choices, new OnSingleCallbackConfirmListener() {
                                                            @Override
                                                            public void onSingleCallbackConfirmed(PanterDialog dialog, int pos, String text) {
                                                                if(text.equals("hazard level 1")){
                                                                    flare.setType("light");
                                                                }else if(text.equals("hazard level 2")){
                                                                    flare.setType("mid");
                                                                }else if(text.equals("hazard level 2")){
                                                                    flare.setType("heavy");
                                                                }
                                                                mDatabase.child(flare.getImagename()).setValue(flare);
                                                                Intent intent = new Intent(LocationSelectionActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        })
                                                        .show();
                                            }
                                        })// You can pass also View.OnClickListener as second param
                                        .setNegative("NO")
                                        .setMessage("Set hazard Location?")
                                        .isCancelable(false).withAnimation(Animation.POP)
                                        .show();

                            }
                        });

    }



}
