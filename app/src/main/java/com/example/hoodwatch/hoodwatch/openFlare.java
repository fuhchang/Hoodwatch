package com.example.hoodwatch.hoodwatch;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapWell;
import com.beardedhen.androidbootstrap.TypefaceProvider;

/**
 * Created by norman on 29/11/16.
 */

public class openFlare extends AppCompatActivity {
    Intent intent;
    double lat;
    double lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_flare);
        TypefaceProvider.registerDefaultIconSets();

        Bundle bundle = getIntent().getExtras();
        String address = bundle.getString("add");
        String text = bundle.getString("post");
        String imagename = bundle.getString("imagename");
        lat = bundle.getDouble("lat");
        lng = bundle.getDouble("long");
        String classification = bundle.getString("classification");
        TextView tv_add = (TextView)findViewById(R.id.tv_openadd);
        TextView tv_post = (TextView)findViewById(R.id.tv_opentext);
        ImageView iv_image = (ImageView)findViewById(R.id.iv_openimage);
        ImageView iv_icon = (ImageView)findViewById(R.id.iv_openicon);
        tv_add.setText(address);
        tv_post.setText(text);
        Flare f = new Flare();
        if(imagename.equals("")){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 30;
            iv_image.setLayoutParams(lp);
        }
        else {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 50;
            iv_image.setLayoutParams(lp);
            iv_image.setImageBitmap(f.loadImageFromStorage("/data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir/", imagename + ".jpg"));
        }
        if(classification.equals("light")){
            iv_icon.setImageResource(this.getResources().getIdentifier("cat1", "mipmap", this.getPackageName()));
        }
        else if(f.getClassification().equals("mid")){
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
