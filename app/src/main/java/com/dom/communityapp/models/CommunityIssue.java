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

    @Expose
    @SerializedName("coordinate")
    public LatLng coordinate;

    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("long_description")
    public String longDescription;

    @Expose
    @SerializedName("short_description")
    public String shortDescription;

    @Expose
    @SerializedName("comments")
    public List<String> comments;

    @Expose
    @SerializedName("tag")
    public String tag;

    @Expose
    @SerializedName("time_duration")
    public String timeDuration;

    @Expose
    @SerializedName("category")
    public String category;

    public Bitmap image;

    public CommunityIssue(String sshort, String llong, String cat_text, String tag_text, String time_text) {
        this.category = cat_text;
        this.tag = tag_text;
        this.timeDuration = time_text;
        this.shortDescription = sshort;
        this.longDescription = llong;
    }
}
