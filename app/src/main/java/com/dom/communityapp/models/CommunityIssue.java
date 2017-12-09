package com.dom.communityapp.models;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by daniel on 12/8/17.
 */

public class CommunityIssue implements Serializable{

    private Image image;

    @Expose
    @SerializedName("coordinate")
    public LatLng coordinate;

    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("long_description")
    public String long_description;

    @Expose
    @SerializedName("short_description")
    public String short_description;

    @Expose
    @SerializedName("comments")
    public List<String> comments;

    @Expose
    @SerializedName("tag")
    public String tag;

    @Expose
    @SerializedName("time_duration")
    public String timed_duration;

    @Expose
    @SerializedName("category")
    public String category;

    public CommunityIssue(String sshort, String llong, String cat_text, String tag_text, String time_text, Image image) {
        this.category = cat_text;
        this.tag = tag_text;
        this.timed_duration = time_text;
        this.short_description= sshort;
        this.long_description = llong;
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
