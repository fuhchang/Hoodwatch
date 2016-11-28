package com.example.hoodwatch.hoodwatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Flare {
    private String flareID, imagename,flareText, classification, userName;
    private long latitude, longtitude, time;


    public Flare(){
        flareID = "";
        imagename = "";
        flareText = "";
        classification = "";
        userName = "";
        latitude = 0L;
        longtitude = 0L;
        time = 0L;
    }

    public String getFlareID() {
        return flareID;
    }

    protected void setFlareID(String flareID) {
        this.flareID = flareID;
    }

    public String getImagename() {
        return imagename;
    }

    protected void setImagename(String imagename) {
        this.imagename = imagename;
    }

    protected String getFlareText() {
        return flareText;
    }

    protected void setFlareText(String flareText) {
        this.flareText = flareText;
    }

    public String getClassification() {
        return classification;
    }
    protected void setClassification(String classification){
        this.classification = classification;
    }

    public String getUserName() {
        return userName;
    }

    protected void setUserName(String userName) {
        this.userName = userName;
    }

    public long getLatitude() {
        return latitude;
    }

    protected void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongtitude() {
        return longtitude;
    }

    protected void setLongtitude(long longtitude) {
        this.longtitude = longtitude;
    }

    public long getTime() {
        return time;
    }

    protected void setTime(long time) {
        this.time = time;
    }

    public Bitmap loadImageFromStorage(String path, String imageName)
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
