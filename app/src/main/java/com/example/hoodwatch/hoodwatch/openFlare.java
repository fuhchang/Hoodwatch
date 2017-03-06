package com.example.hoodwatch.hoodwatch;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapWell;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by norman on 29/11/16.
 */

public class openFlare extends AppCompatActivity {
    Intent intent;
    double lat;
    double lng;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_flare);
        TypefaceProvider.registerDefaultIconSets();

        Bundle bundle = getIntent().getExtras();
        String address = bundle.getString("add");
        String text = bundle.getString("post");
        final String imagename = bundle.getString("imagename");
        lat = bundle.getDouble("lat");
        lng = bundle.getDouble("long");
        String classification = bundle.getString("classification");
        TextView tv_add = (TextView)findViewById(R.id.tv_openadd);
        TextView tv_post = (TextView)findViewById(R.id.tv_opentext);
        final ImageView iv_image = (ImageView)findViewById(R.id.iv_openimage);
        ImageView iv_icon = (ImageView)findViewById(R.id.iv_openicon);
        tv_add.setText(address);
        tv_post.setText(text);
//        if(imagename.equals("")){
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            lp.topMargin = 30;
//            iv_image.setLayoutParams(lp);
//        }
//        else {
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            lp.topMargin = 50;
//            iv_image.setLayoutParams(lp);
//            iv_image.setImageBitmap(f.loadImageFromStorage("/data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir/", imagename + ".jpg"));
//        }


        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imagesRef = mStorageRef.child(imagename +".jpg");
        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("firebase success img",uri.getPath());
//                Picasso.with(mContext).load(uri).rotate(90).into(holder.iv);
                Glide.with(getApplication()).load(uri).centerCrop().crossFade().into(iv_image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i("firebase error img ",exception.getMessage());
            }
        });
        Log.d("class", classification);
        if(classification.equals("light")){
            iv_icon.setImageResource(this.getResources().getIdentifier("cat1", "mipmap", this.getPackageName()));
        }
        else if(classification.equals("mid")){
            iv_icon.setImageResource(this.getResources().getIdentifier("cat2", "mipmap", this.getPackageName()));
        }
        else{
            iv_icon.setImageResource(this.getResources().getIdentifier("cat3", "mipmap", this.getPackageName()));
        }
        //tv_add.setOnClickListener(new onClickopenFrag(bundle.getLong("lat"), bundle.getLong("long"), this));
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(openFlare.this,MapActivity.class);
                intent.putExtra("lat",lat);
                intent.putExtra("long",lng);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(openFlare.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

