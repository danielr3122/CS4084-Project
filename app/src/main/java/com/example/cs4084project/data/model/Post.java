package com.example.cs4084project.data.model;

import android.graphics.Bitmap;

public class Post{
    private String caption;
    //private GoogleMapsThing mapsThing;
    private Bitmap image;

    public Post(Bitmap image, String caption){
        this.image = image;
        this.caption = caption;
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
    }
}
