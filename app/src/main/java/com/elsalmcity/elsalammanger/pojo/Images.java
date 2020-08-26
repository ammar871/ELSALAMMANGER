package com.elsalmcity.elsalammanger.pojo;

import android.net.Uri;

public class Images {
 private   Uri image;

    public Images(Uri image) {
        this.image = image;
    }

    public Images() {
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
