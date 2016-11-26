package com.example.hoodwatch.hoodwatch;

import android.content.res.AssetManager;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] images = new String[0];
        try {
            images = getAssets().list("images");
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 5f);
        lp1.width=LinearLayout.LayoutParams.MATCH_PARENT;
        lp1.height= 550;
        lp1.gravity = Gravity.CENTER;
        lp1.bottomMargin = 20;


        for(int i=3;i< images.length;i++){
            InputStream inputstream= null;
            try {
                inputstream = this.getAssets().open("images1/"+images[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Drawable drawable = Drawable.createFromStream(inputstream, null);

            CardView cv = new CardView(this);
            TextView tv = new TextView(this);
            ImageView iv = new ImageView(this);
            tv.setText("");
            iv.setImageDrawable(drawable);
            tv.setTextColor(Color.BLACK);
            cv.setCardBackgroundColor(Color.WHITE);
            tv.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));

            iv.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
            if(tv.getParent()!=null) {
                ((ViewGroup) tv.getParent()).removeView(tv); // <- fix
                ((ViewGroup) iv.getParent()).removeView(iv); // <- fix
            }
            cv.addView(tv);
            cv.addView(iv);
            cv.setLayoutParams(lp1);
            ll.addView(cv);


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
    private void addflarewithImage(){

    }
}
