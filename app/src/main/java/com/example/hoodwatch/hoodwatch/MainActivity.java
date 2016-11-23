package com.example.hoodwatch.hoodwatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = 350;
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp1.width=LinearLayout.LayoutParams.MATCH_PARENT;
        lp1.height=700;

        for(int i=0;i<20;i++){
            TextView tv = new TextView(this);
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.image1);
            tv.setText("hello");
            CardView cv = new CardView(this);
            lp.weight=1;

            tv.setLayoutParams(lp);
            lp.gravity = Gravity.END;
            iv.setLayoutParams(lp);
            cv.addView(iv,0);
            cv.addView(tv, 1);
            cv.setLayoutParams(lp1);
            ll.addView(cv, i);
        }


       /* CustomList adapter = new CustomList(MainActivity.this, web, imageId);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();

            }
        });*/
    }
}
