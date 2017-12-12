package com.dom.communityapp.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by daniel on 12/8/17.
 */

public class CommunityIssue implements Serializable{

    public IssueImage issueImage;

    public Location coordinate;

    public String name;

    public String long_description;

    public String short_description;

    public List<String> comments;

    public String tag;

    public String timed_duration;

    public String category;

    @Exclude
    public String firebaseID;

    //Default constructor required by firebase, just like the getters and setters for all the things
    public CommunityIssue() {
    }

    public CommunityIssue(String sshort, String llong, String cat_text, String tag_text, String time_text, IssueImage issueImage) {
        this.category = cat_text;
        this.tag = tag_text;
        this.timed_duration = time_text;
        this.short_description= sshort;
        this.long_description = llong;
        this.issueImage = issueImage;
    }

    public CommunityIssue(String sshort, String llong, String cat_text, String tag_text, String time_text, IssueImage issueImage, Location coordinate) {
        this.category = cat_text;
        this.tag = tag_text;
        this.timed_duration = time_text;
        this.short_description= sshort;
        this.long_description = llong;
        this.issueImage = issueImage;
        this.coordinate = coordinate;
    }

    public CommunityIssue(String sshort, String llong, String cat_text, String tag_text, String time_text) {
        this.category = cat_text;
        this.tag = tag_text;
        this.timed_duration = time_text;
        this.short_description= sshort;
        this.long_description = llong;
    }


    public Location getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Location coordinate) {
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLong_description() {
        return long_description;
    }

    public void setLong_description(String long_description) {
        this.long_description = long_description;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTimed_duration() {
        return timed_duration;
    }

    public void setTimed_duration(String timed_duration) {
        this.timed_duration = timed_duration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public IssueImage getIssueImage() {
        return issueImage;
    }

    public void setIssueImage(IssueImage issueImage) {
        this.issueImage = issueImage;
    }

    @Exclude
    public String getFirebaseID() {
        return firebaseID;
    }

    @Exclude
    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }
}
