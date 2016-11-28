package com.example.hoodwatch.hoodwatch;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.BackgroundColorSpan;
import android.util.TypedValue;
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
    private flareAdapter adapter;
    private RecyclerView rv;
    LinearLayout ll;
    int maxcount=0;
    String path = "data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir";
    FloatingActionButton myFab ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll= (LinearLayout) findViewById(R.id.linearLayout);
        rv = (RecyclerView) findViewById(R.id.rv_main);
        adapter = new flareAdapter(this, allFlares);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rv.setLayoutManager(mLayoutManager);
        rv.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
        myFab = (FloatingActionButton)findViewById(R.id.addFlare);
        myFab.setOnClickListener((new View.OnClickListener(){
            public void onClick(View v){
                //call activity add flare
                maxcount = loadCSV();
                adapter.notifyDataSetChanged();
                Intent intent = new Intent(MainActivity.this,CreateFlareType.class);
                intent.putExtra("username","norman");
                startActivity(intent);
                finish();
            }
        }));
        maxcount = loadCSV();
        adapter.notifyDataSetChanged();
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    //    private void drawCanvas(){
//        for(String id:allFlares.keySet()){
//            if(allFlares.get(id).getImagename()!=""){
//                drawFlarewithImage(allFlares.get(id));
//            }
//            else{
//                drawFlarewithoutImage(allFlares.get(id));
//            }
//        }
//    }
//    private  void drawSample(){
//        View v = getLayoutInflater().inflate(R.layout.row_layout, null);
//        CardView cv = (CardView)v.findViewById(R.id.flareCards);
//        TextView tv = (TextView)v.findViewById(R.id.tv_Post);
//        ImageView iv = (ImageView)v.findViewById(R.id.iv_image);
//        tv.setText("Hello");
//        iv.setImageBitmap(loadImageFromStorage("assets/images1", "image1"));
//        tv.setTextColor(Color.BLACK);
//        cv.setCardBackgroundColor(Color.WHITE);
//        cv.addView(tv);
//        cv.addView(iv);
//        adapter.notifyDataSetChanged();
//    }
//    private void drawFlarewithImage(Flare drawthisflare){
//        View v = getLayoutInflater().inflate(R.layout.row_layout, null);
//        CardView cv = (CardView)v.findViewById(R.id.flareCards);
//        RecyclerView rv = new RecyclerView(this);
//        TextView tv = (TextView)v.findViewById(R.id.tv_Post);
//        ImageView iv = (ImageView)v.findViewById(R.id.iv_image);
//
//        tv.setText(drawthisflare.getFlareText());
//        iv.setImageBitmap(loadImageFromStorage(path, drawthisflare.getImagename()));
//        tv.setTextColor(Color.BLACK);
//        cv.setCardBackgroundColor(Color.WHITE);
//        cv.addView(tv);
//        cv.addView(iv);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 5f);
//        lp.width=LinearLayout.LayoutParams.MATCH_PARENT;
//        lp.height= 550;
//        lp.gravity = Gravity.CENTER;
//        lp.bottomMargin = 20;
//        rv.setLayoutParams(lp);
//        rv.addView(cv);
//        rv.setOnTouchListener(new OnSwipeTouchListener(this){
//            public void onSwipeLeft() {
//                //call show Flare map;
//            }
//        });
//        ll.addView(rv);
//
//    }
//    private  void drawFlarewithoutImage(Flare drawthisflare){
//        View v = getLayoutInflater().inflate(R.layout.row_layout, null);
//        CardView cv = (CardView)v.findViewById(R.id.flareCards);
//        RecyclerView rv = new RecyclerView(this);
//        TextView tv = (TextView)v.findViewById(R.id.tv_Post);
//        tv.setText(drawthisflare.getFlareText());
//        tv.setTextColor(Color.BLACK);
//        cv.setCardBackgroundColor(Color.WHITE);
//        cv.addView(tv);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 5f);
//        lp.width=LinearLayout.LayoutParams.MATCH_PARENT;
//        lp.height= 550;
//        lp.gravity = Gravity.CENTER;
//        lp.bottomMargin = 20;
//        rv.setLayoutParams(lp);
//        rv.addView(cv);
//        rv.setOnTouchListener(new OnSwipeTouchListener(this){
//            public void onSwipeLeft() {
//                //call show Flare map;
//            }
//        });
//        ll.addView(rv);
//
//    }
    private int loadCSV(){
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
            return count;
        }
        catch (Exception e) {

        }
        return 0;
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