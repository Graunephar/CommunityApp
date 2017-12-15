package com.dom.communityapp.models;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by daniel on 12/9/17.
 */

public class IssueImage implements Serializable {

    @Exclude
    private transient Bitmap bitmap;

    @Exclude
    private String localFilePath;

    private String image_URL;

    public IssueImage() {
    }

    public IssueImage(Uri filePath) {

        this.localFilePath = String.valueOf(filePath);
    }

    public IssueImage(Uri filePath, Bitmap bitmap) {
        this.localFilePath = String.valueOf(filePath);
        this.bitmap = bitmap;
    }


    @Exclude
    public String getLocalFilePath() {
        return localFilePath;
    }

    @Exclude
    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getImage_URL() {
        return image_URL;
    }

    public void setImage_URL(String image_URL) {
        this.image_URL = image_URL;
    }

    @Exclude
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Exclude
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Exclude
    public Uri getFilePathAsURi() {
        return Uri.parse(localFilePath);
    }

    @Exclude
    public void setFilePathAsUri(Uri filepath) {
        localFilePath = String.valueOf(filepath);
    }

    @Exclude
    public Uri getURLAsUri() {
        return Uri.parse(image_URL);
    }

    @Exclude
    public void setURLasUri(Uri url) {
        image_URL = String.valueOf(url);
    }
}
