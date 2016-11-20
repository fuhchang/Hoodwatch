package com.example.hoodwatch.hoodwatch;


import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment f = Fragment.instantiate(this,MapFragment.class.getName());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container,f).commit();
    }
}
