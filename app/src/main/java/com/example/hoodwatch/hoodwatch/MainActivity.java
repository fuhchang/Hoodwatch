package com.example.hoodwatch.hoodwatch;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    HashMap<String, Flare> allFlares = new HashMap<String, Flare>();
    LinearLayout ll;

    String path = "data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir";
    FloatingActionButton myFab ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll= (LinearLayout) findViewById(R.id.linearLayout);
        myFab = (FloatingActionButton)findViewById(R.id.addFlare);
        myFab.setOnClickListener((new View.OnClickListener(){
            public void onClick(View v){
                //call activity add flare
                loadCSV();
                drawCanvas();
            }
        }));
        loadCSV();
        drawCanvas();
    }

    private void drawCanvas(){
        for(String id:allFlares.keySet()){
            if(allFlares.get(id).getImagename()!=""){
                drawFlarewithImage(allFlares.get(id));
            }
            else{
                drawFlarewithoutImage(allFlares.get(id));
            }
        }
    }
    private void drawFlarewithImage(Flare drawthisflare){
        View v = getLayoutInflater().inflate(R.layout.row_layout, null);
        CardView cv = (CardView)v.findViewById(R.id.flareCards);
        RecyclerView rv = (RecyclerView)v.findViewById(R.id.flareRecycler);
        TextView tv = (TextView)v.findViewById(R.id.tv_Post);
        ImageView iv = (ImageView)v.findViewById(R.id.iv_image);

        tv.setText(drawthisflare.getFlareText());
        iv.setImageBitmap(loadImageFromStorage(path, drawthisflare.getImagename()));
        tv.setTextColor(Color.BLACK);
        cv.setCardBackgroundColor(Color.WHITE);
        cv.addView(tv);
        cv.addView(iv);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 5f);
        lp.width=LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height= 550;
        lp.gravity = Gravity.CENTER;
        lp.bottomMargin = 20;
        rv.setLayoutParams(lp);
        rv.addView(cv);
        ll.addView(rv);

    }
    private  void drawFlarewithoutImage(Flare drawthisflare){
        View v = getLayoutInflater().inflate(R.layout.row_layout, null);
        CardView cv = (CardView)v.findViewById(R.id.flareCards);
        RecyclerView rv = (RecyclerView)v.findViewById(R.id.flareRecycler);
        TextView tv = (TextView)v.findViewById(R.id.tv_Post);
        tv.setText(drawthisflare.getFlareText());
        tv.setTextColor(Color.BLACK);
        cv.setCardBackgroundColor(Color.WHITE);
        cv.addView(tv);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 5f);
        lp.width=LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height= 550;
        lp.gravity = Gravity.CENTER;
        lp.bottomMargin = 20;
        rv.setLayoutParams(lp);
        rv.addView(cv);
        ll.addView(rv);

    }
    private void loadCSV(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File("data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir/flares.csv")));
            String csvLine;
            int count=0;
            ArrayList<String[]> allRows = new ArrayList<>();
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                allRows.add(count,row);
                count++;
            }
            for(int i=0 ; i < count; i++){
                String[] row = allRows.get(i);
                Flare f = new Flare();
                f.setFlareID(row[0]);
                f.setImagename(row[1]);
                f.setFlareText(row[2]);
                f.setClassification(row[3]);
                f.setUserName(row[4]);
                f.setLatitude(Long.parseLong(row[5]));
                f.setLongtitude(Long.parseLong(row[6]));
                f.setTime(Long.parseLong(row[7]));
                allFlares.put(row[0], f);
            }
        }
        catch (Exception e) {

        }
    }

    private Bitmap loadImageFromStorage(String path, String imageName)
    {
        Bitmap b = null;
        try {
            File f=new File(path, imageName);
            b = BitmapFactory.decodeStream(new FileInputStream(f));


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }
}
