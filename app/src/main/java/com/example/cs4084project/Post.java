package com.example.cs4084project;


import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Post{
    private String caption;
    //private GoogleMapsThing mapsThing;
    private Bitmap image;
    private String imageStr;
    private double longitude;
    private double latitude;
    private final boolean containsLocation;

    private final String userUID;
    private final String userNickname;

    public Post(Bitmap image, String caption, String userUID, String userNickname){
        this.image = image;
        this.imageStr = getStringFromBitmap(image);
        this.caption = caption;
        this.userUID = userUID;
        this.userNickname = userNickname;
        containsLocation = false;
    }

    public Post(Bitmap image, String caption, double longitude, double latitude, String userUID, String userNickname){
        this.image = image;
        this.imageStr = getStringFromBitmap(image);
        this.caption = caption;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userUID = userUID;
        this.userNickname = userNickname;
        containsLocation = true;
    }


    public String getCaption(){
        return caption;
    }


    public Bitmap getImage(){
        if(image != null){
            return image;
        } else {
            return null;
        }
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setImage(Bitmap image) {

        this.image = image;
        this.imageStr = getStringFromBitmap(image);
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public boolean hasLocation(){
        return containsLocation;
    }

    public String getUserUID(){
        return userUID;
    }

    public String getUserNickname(){
        return userNickname;
    }

    public String getImageStr() {
        return imageStr;
    }
}



