package com.example.hoodwatch.hoodwatch;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.crypto.spec.RC2ParameterSpec;

/**
 * Created by norman on 28/11/16.
 */


public class flareAdapter extends RecyclerView.Adapter<flareAdapter.MyViewHolder> {

    private List<Flare> allFlares = new ArrayList<>();
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv, tv_add;
        public ImageView iv, iv_icon;
        public CardView cv;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_Post);
            iv = (ImageView) itemView.findViewById(R.id.iv_image);
            cv = (CardView) itemView.findViewById(R.id.flareCards);
            tv_add = (TextView) itemView.findViewById(R.id.tv_address);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            cv.setCardBackgroundColor(Color.WHITE);
        }
    }
    public flareAdapter(Context mContext, ArrayList<Flare> allFlares){
        this.mContext = mContext;
        this.allFlares = allFlares;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Flare f = allFlares.get(position);
        holder.tv.setText(f.getFlareText());
        holder.tv.setTextSize(30);
        holder.tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        holder.tv_add.setText(f.getAddress());
        holder.iv.setImageBitmap(f.loadImageFromStorage("/data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir/", f.getImagename()+".jpg"));
        if((f.loadImageFromStorage("/data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir/", f.getImagename()+".jpg") == null)){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 30;
            holder.iv.setLayoutParams(lp);
        }
        else{
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 600);
            lp.topMargin = 50;

            holder.iv.setLayoutParams(lp);
        }

        if(f.getClassification().equals("light")){
            holder.iv_icon.setImageResource(mContext.getResources().getIdentifier("cat1", "mipmap", mContext.getPackageName()));
        }
        else if(f.getClassification().equals("mid")){
            holder.iv_icon.setImageResource(mContext.getResources().getIdentifier("cat2", "mipmap", mContext.getPackageName()));
        }
        else{
            holder.iv_icon.setImageResource(mContext.getResources().getIdentifier("cat3", "mipmap", mContext.getPackageName()));
        }

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return allFlares.size();
    }
}
