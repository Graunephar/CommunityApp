package com.dom.communityapp.models;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by daniel on 12/9/17.
 */

public class Image implements Serializable{

    public Bitmap bitmap;
    public Uri localFilePath;
    public Uri image_URL;

    public Image() {
    }

    public Image(Bitmap bitmap, Uri filePath) {
        this.bitmap = bitmap;
        this.localFilePath = filePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(Uri localFilePath) {
        this.localFilePath = localFilePath;
    }

    public Uri getImage_URL() {
        return image_URL;
    }

    public void setImage_URL(Uri image_URL) {
        this.image_URL = image_URL;
    }
}
