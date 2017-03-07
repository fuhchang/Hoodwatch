package com.example.hoodwatch.hoodwatch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import test.jinesh.loadingviews.LoadingImageView;
import test.jinesh.loadingviews.LoadingTextView;



/**
 * Created by norman on 28/11/16.
 */


public class flareAdapter extends RecyclerView.Adapter<flareAdapter.MyViewHolder> {

    private List<Flare> allFlares = new ArrayList<>();
    private Context mContext;
    private StorageReference mStorageRef;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public LoadingImageView iv;
        public LoadingTextView tv_add;
        public LoadingTextView tv;
        public LoadingImageView  iv_icon;
        public CardView cv;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (LoadingTextView) itemView.findViewById(R.id.tv_Post);
            tv.startLoading();
            iv = (LoadingImageView) itemView.findViewById(R.id.iv_image);
            iv.startLoading();
            cv = (CardView) itemView.findViewById(R.id.flareCards);
            tv_add = (LoadingTextView) itemView.findViewById(R.id.tv_address);
            tv_add.startLoading();
            iv_icon = (LoadingImageView) itemView.findViewById(R.id.iv_icon);
            iv_icon.startLoading();
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
        holder.tv.stopLoading();
        holder.tv.setTextSize(30);
        holder.tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        holder.tv_add.setText(f.getAddress());
        holder.tv_add.stopLoading();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imagesRef = mStorageRef.child(f.getImagename()+".jpg");
        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("firebase success img",uri.getPath());
                Glide.with(mContext).load(uri).centerCrop().crossFade().into(holder.iv);
                holder.iv.stopLoading();
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
        holder.iv_icon.stopLoading();
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
        intent.putExtra("flare",f);
        mContext.startActivity(intent);

    }



}