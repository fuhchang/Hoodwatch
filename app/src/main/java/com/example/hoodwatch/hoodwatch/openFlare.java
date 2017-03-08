package com.example.hoodwatch.hoodwatch;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import test.jinesh.loadingviews.LoadingImageView;
import test.jinesh.loadingviews.LoadingTextView;

/**
 * Created by norman on 29/11/16.
 */

public class openFlare extends AppCompatActivity {
    Intent intent;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_flare);
        TypefaceProvider.registerDefaultIconSets();


        LoadingTextView tv_add = (LoadingTextView) findViewById(R.id.tv_openadd);
        tv_add.startLoading();
        LoadingTextView  tv_post = (LoadingTextView) findViewById(R.id.tv_opentext);
        tv_post.startLoading();
        final LoadingImageView iv_image = (LoadingImageView) findViewById(R.id.iv_openimage);
        iv_image.startLoading();
        LoadingImageView iv_icon = (LoadingImageView) findViewById(R.id.iv_openicon);
        iv_icon.startLoading();

        String add = "", text1="";
        flare = (Flare) getIntent().getSerializableExtra("flare");
        if(flare== null) {
        }
        else {
            try {
                add = flare.getAddress();
                text1 = flare.getflareText();
            } catch (Exception e) {
                e.printStackTrace();
            }
            tv_add.setText(add);
            tv_add.stopLoading();
            tv_post.setText(text1);
            tv_post.stopLoading();

            mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imagesRef = mStorageRef.child(flare.getImagename());
        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("firebase success img",uri.getPath());
                Glide.with(getApplication()).load(uri).centerCrop().crossFade().into(iv_image);
                iv_image.stopLoading();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i("firebase error img ",exception.getMessage());
            }
        });
        Log.d("class", flare.getClassification());
        if(flare.getClassification().equals("light")){
            iv_icon.setImageResource(this.getResources().getIdentifier("cat1", "mipmap", this.getPackageName()));
        }
        else if(flare.getClassification().equals("mid")){
            iv_icon.setImageResource(this.getResources().getIdentifier("cat2", "mipmap", this.getPackageName()));
        }
        else{
            iv_icon.setImageResource(this.getResources().getIdentifier("cat3", "mipmap", this.getPackageName()));
        }
        iv_icon.stopLoading();
        //tv_add.setOnClickListener(new onClickopenFrag(bundle.getLong("lat"), bundle.getLong("long"), this));
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(openFlare.this,MapActivity.class);
                intent.putExtra("lat",flare.getLatitude());
                intent.putExtra("long",flare.getLongtitude());
                startActivity(intent);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap map) {
        System.out.println(flare.getLatitude()+":"+flare.getLongtitude());
        LatLng latlngofFlare = new LatLng(flare.getLatitude(), flare.getLongtitude());
        map.addMarker(new MarkerOptions()
                .position(latlngofFlare)
                .title(flare.getflareText()));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngofFlare,15));

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(openFlare.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

