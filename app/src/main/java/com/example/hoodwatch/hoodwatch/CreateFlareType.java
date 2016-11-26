package com.example.hoodwatch.hoodwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.beardedhen.androidbootstrap.BootstrapWell;
import com.beardedhen.androidbootstrap.TypefaceProvider;


public class CreateFlareType extends AppCompatActivity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flare_type);
        TypefaceProvider.registerDefaultIconSets();
        intent = new Intent(this, CreateFlareMain.class);
        BootstrapWell lightWell = (BootstrapWell) findViewById(R.id.light);
        lightWell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("well", "light well");
                startActivity(intent);
            }
        });

        BootstrapWell midWell = (BootstrapWell) findViewById(R.id.mid);
        midWell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("well", "mid well");
                startActivity(intent);
            }
        });

        BootstrapWell lightheavy = (BootstrapWell) findViewById(R.id.heavy);
        lightheavy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("well", "heavy well");
                startActivity(intent);
            }
        });
    }
}
