package com.example.hoodwatch.hoodwatch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.crypto.spec.RC2ParameterSpec;

import static android.support.v4.content.ContextCompat.startActivity;
import static com.bumptech.glide.Glide.with;

/**
 * Created by norman on 28/11/16.
 */


public class flareAdapter extends RecyclerView.Adapter<flareAdapter.MyViewHolder> {

    private List<Flare> allFlares = new ArrayList<>();
    private Context mContext;
    private StorageReference mStorageRef;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv, tv_add, tv_lat, tv_long;
        public ImageView iv, iv_icon;
        public CardView cv;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_Post);
            iv = (ImageView) itemView.findViewById(R.id.iv_image);
            cv = (CardView) itemView.findViewById(R.id.flareCards);
            tv_add = (TextView) itemView.findViewById(R.id.tv_address);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_lat = (TextView) itemView.findViewById(R.id.tv_lat);
            tv_long = (TextView) itemView.findViewById(R.id.tv_long);
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Flare f = allFlares.get(position);
        holder.tv.setText(f.getflareText());
        holder.tv.setTextSize(30);
        holder.tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        holder.tv_add.setText(f.getAddress());
        holder.tv_lat.setText(f.getLatitude().toString());
        holder.tv_long.setText(f.getLongtitude().toString());
//        Log.d("Address: ", f.getAddress());
//        holder.iv.setImageBitmap(f.loadImageFromStorage("/data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir/", f.getImagename()+".jpg"));
//        if((f.loadImageFromStorage("/data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir/", f.getImagename()+".jpg") == null)){
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            lp.topMargin = 30;
//            holder.iv.setLayoutParams(lp);
//        }
//        else{
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 600);
//            lp.topMargin = 50;
//
//            holder.iv.setLayoutParams(lp);
//        }

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imagesRef = mStorageRef.child(f.getImagename()+".jpg");
        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("firebase success img",uri.getPath());
                Picasso.with(mContext).load(uri).into(holder.iv);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i("firebase error img ",exception.getMessage());
            }
        });

        if(f.getType().equals("light")){
            holder.iv_icon.setImageResource(mContext.getResources().getIdentifier("cat1", "mipmap", mContext.getPackageName()));
        }
        else if(f.getType().equals("mid")){
            holder.iv_icon.setImageResource(mContext.getResources().getIdentifier("cat2", "mipmap", mContext.getPackageName()));
        }
        else{
            holder.iv_icon.setImageResource(mContext.getResources().getIdentifier("cat3", "mipmap", mContext.getPackageName()));
        }

        holder.cv.setOnClickListener(new myOwnClickListener(f, mContext));
    }

    @Override
    public int getItemCount() {
        return allFlares.size();
    }
}
class myOwnClickListener implements View.OnClickListener
{

    Flare f;
    Context mContext;
    public myOwnClickListener(Flare f, Context mContext) {
        this.f = f;
        this.mContext = mContext;
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(mContext, openFlare.class);
        Log.d("address :", f.getAddress());
        intent.putExtra("add", f.getAddress());
        intent.putExtra("post", f.getflareText());
        intent.putExtra("imagename", f.getImagename());
        intent.putExtra("classification", f.getClassification());
        intent.putExtra("lat", f.getLatitude());
        intent.putExtra("long", f.getLongtitude());
        mContext.startActivity(intent);

    }



}