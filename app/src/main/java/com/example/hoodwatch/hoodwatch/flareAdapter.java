package com.example.hoodwatch.hoodwatch;

import android.content.Context;
import android.content.DialogInterface;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eminayar.panter.PanterDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import test.jinesh.loadingviews.LoadingImageView;
import test.jinesh.loadingviews.LoadingTextView;




public class flareAdapter extends RecyclerView.Adapter<flareAdapter.MyViewHolder> {
    public flareAdapter thisAdapter = this;
    private List<Flare> allFlares = new ArrayList<>();
    private Context mContext;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private Flare flare1;
    private int lastPosition = -1;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public LoadingImageView iv, tv_hide;
        public LoadingTextView tv_add;
        public LoadingTextView tv;
        public LoadingImageView  iv_icon;
        public CardView cv;
        public LoadingTextView tv_distance;


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
            tv_distance = (LoadingTextView) itemView.findViewById(R.id.tv_Distance);
            tv_hide = (LoadingImageView) itemView.findViewById(R.id.tv_hide);
            cv.setCardBackgroundColor(Color.WHITE);
        }
    }
    public flareAdapter(Context mContext, ArrayList<Flare> allFlares){
        this.mContext = mContext;
        this.allFlares = allFlares;
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Flare f = allFlares.get(position);
        try {
            holder.tv.setText(f.getflareText());
            holder.tv.stopLoading();
            holder.tv.setTextSize(30);
            holder.tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            setAnimation(holder.itemView, position);
        }
        catch (Exception e){
            holder.tv.setText("");
        }
        holder.tv_add.setText(f.getAddress());
        holder.tv_add.stopLoading();
        holder.tv_distance.setText(Double.toString(f.getFlareDistance())+" metres");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        StorageReference imagesRef = mStorageRef.child(f.getImagename());
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

        final PanterDialog pd = new PanterDialog(mContext);
        pd.setPositive("Yes", new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                f.setHideFrom(f.getUserName());
                pd.dismiss();
                mDatabase.child(f.getFlareID()).child("hideFrom").setValue(f.getHideFrom());
                thisAdapter.notifyItemRemoved(position);
            }
        })// You can pass also View.OnClickListener as second param
                .setNegative("DISMISS")
                .setMessage("Do you want to hide this flare?")
                .isCancelable(false);
        holder.tv_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
            }
        });


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

        Intent intent = new Intent(mContext, ViewFlare.class);
        intent.putExtra("flare",f);
        mContext.startActivity(intent);

    }



}