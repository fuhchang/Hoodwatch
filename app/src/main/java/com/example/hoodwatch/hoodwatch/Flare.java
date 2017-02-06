package com.example.hoodwatch.hoodwatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

public class Flare implements Serializable {
    private String flareID, imagename,flareText, classification, userName, address;
    private Double latitude, longtitude;
    private long time;
    private String type;

    public Flare(){
        flareID = "";
        imagename = "";
        flareText = "";
        classification = "";
        userName = "";
        address="";
        latitude = 0.0;
        longtitude = 0.0;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    protected void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getLatitude() {
        return latitude;
    }

    protected void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    protected void setLongtitude(Double longtitude) {
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
            imagename="";
            b = null;
        }
        finally {
            return b;
        }

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
