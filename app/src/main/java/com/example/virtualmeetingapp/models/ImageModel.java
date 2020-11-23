package com.example.virtualmeetingapp.models;

import android.net.Uri;

public class ImageModel {

    Uri image;
    String imagename;

    public ImageModel() {
    }


    public ImageModel(String imagename, Uri image) {
        this.imagename = imagename;
        this.image = image;
    }


    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }
}
