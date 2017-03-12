package com.example.hoodwatch.hoodwatch;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.github.yuweiguocn.lib.squareloading.SquareLoading;

import static com.example.hoodwatch.hoodwatch.R.mipmap.flare;

public class LocationSelectionActivity extends FragmentActivity implements OnMapReadyCallback {

    Uri imageUri;
    Marker marker;
    Circle circle;
    Flare flare;
    SquareLoading sl;
    ImageView thumb;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);
        Bundle bundle = getIntent().getExtras();
        flare = (Flare) getIntent().getSerializableExtra("flare");
        if(bundle != null && bundle.containsKey("imageUri")){
            imageUri = (Uri) bundle.get("imageUri");
            Log.d("img path", imageUri.toString());
        }


        mDatabase = FirebaseDatabase.getInstance().getReference().push();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        thumb = (ImageView) findViewById(R.id.thumb);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        new PanterDialog(this)
                .setHeaderBackground(R.color.colorPrimary)
                .setHeaderLogo(R.mipmap.icon1)
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
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> list  = geocoder.getFromLocation(flare.getLatitude(),flare.getLongtitude(),1);
                    if(list.size() != 0) {
                        flare.setAddress(list.get(0).getAddressLine(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                thumb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new PanterDialog(LocationSelectionActivity.this)
                                .setHeaderBackground(R.color.colorGrey)
                                .setHeaderLogo(R.mipmap.icon1)
                                .setDialogType(DialogType.SINGLECHOICE)
                                .isCancelable(false).withAnimation(Animation.POP)

                                .items(R.array.choices, new OnSingleCallbackConfirmListener() {
                                    @Override
                                    public void onSingleCallbackConfirmed(PanterDialog dialog, int pos, String text) {
                                        sl = (SquareLoading) findViewById(R.id.SquareLoading);
                                        if(text.contains("Not Dangerous")){
                                            flare.setType("light");
                                        }else if(text.contains("Mildly Dangerous")){
                                            flare.setType("mid");
                                        }else if(text.contains("Highly Dangerous")){
                                            flare.setType("heavy");
                                        }
                                        mDatabase.setValue(flare);
                                        flare.setImagename(mDatabase.getKey()+".jpg");
                                        flare.setFlareID(mDatabase.getKey());
                                        mDatabase.setValue(flare);
                                        StorageReference imgRef = mStorageRef.child(flare.getImagename());
                                        UploadTask uploadTask = imgRef.putFile(imageUri);
                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(LocationSelectionActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                sl.setVisibility(View.GONE);
                                                finish();
                                            }
                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                            }
                                        });

                                    }
                                })
                                .show();
                    }
                });

                            }
                        });

    }



}
