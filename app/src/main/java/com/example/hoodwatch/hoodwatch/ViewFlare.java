package com.example.hoodwatch.hoodwatch;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import test.jinesh.loadingviews.LoadingImageView;
import test.jinesh.loadingviews.LoadingTextView;

public class ViewFlare extends AppCompatActivity {
    private StorageReference mStorageRef;
    private Context mContext;
    private Activity mActivity;

    private CoordinatorLayout mCLayout;
    private FloatingActionButton mFAB;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_flare);
        // Get the application context
        final Flare flare = (Flare) getIntent().getSerializableExtra("flare");
        mContext = getApplicationContext();
        mActivity = ViewFlare.this;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // Get the widget reference from XML layout
        mCLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mFAB = (FloatingActionButton) findViewById(R.id.fab);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        final ImageView iv_image = (ImageView) findViewById(R.id.image_view);
        // Set the support action bar
        setSupportActionBar(mToolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set a title for collapsing toolbar layout
        LoadingTextView tv_add = (LoadingTextView) findViewById(R.id.tv_openadd);
        tv_add.startLoading();
        LoadingTextView  tv_post = (LoadingTextView) findViewById(R.id.tv_opentext);
        tv_post.startLoading();
        LoadingImageView iv_icon = (LoadingImageView) findViewById(R.id.iv_openicon);
        iv_icon.startLoading();
        tv_add.setText(flare.getAddress());
        tv_add.stopLoading();
        tv_post.setText(flare.getflareText());
        tv_post.stopLoading();
        if (flare.getType().equals("light")) {
            iv_icon.setImageResource(this.getResources().getIdentifier("cat1", "mipmap", this.getPackageName()));
        } else if (flare.getType().equals("mid")) {
            iv_icon.setImageResource(this.getResources().getIdentifier("cat2", "mipmap", this.getPackageName()));
        } else {
            iv_icon.setImageResource(this.getResources().getIdentifier("cat3", "mipmap", this.getPackageName()));
        }
        iv_icon.stopLoading();
        StorageReference imagesRef = mStorageRef.child(flare.getImagename());
        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("firebase success img",uri.getPath());
                Glide.with(getApplication()).load(uri).centerCrop().crossFade().into(iv_image);
                mCollapsingToolbarLayout.setTitle("HOODWATCH");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i("firebase error img ",exception.getMessage());
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                LatLng latlngofFlare = new LatLng(flare.getLatitude(), flare.getLongtitude());
                map.addMarker(new MarkerOptions()
                        .position(latlngofFlare)
                        .title(flare.getflareText()));

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngofFlare,18));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }
}
