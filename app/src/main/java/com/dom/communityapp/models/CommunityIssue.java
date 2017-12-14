package com.dom.communityapp.models;


import com.dom.communityapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

/**
 * Created by daniel on 12/8/17.
 */

public class CommunityIssue implements Serializable {

    private IssueImage issueImage;

    @Exclude
    private LatLng coordinate;

    private String name;

    private String long_description;

    private String short_description;

    private List<String> comments;

    private String tag;

    private String timed_duration;

    private String category;

    @Exclude
    private String firebaseID;

    //Default constructor required by firebase, just like the getters and setters for all the things
    public CommunityIssue() {
    }

    public CommunityIssue(String sshort, String llong, String cat_text, String tag_text, String time_text, IssueImage issueImage) {
        this.category = cat_text;
        this.tag = tag_text;
        this.timed_duration = time_text;
        this.short_description = sshort;
        this.long_description = llong;
        this.issueImage = issueImage;
    }

    public CommunityIssue(String sshort, String llong, String cat_text, String tag_text, String time_text, IssueImage issueImage, LatLng coordinate) {
        this.category = cat_text;
        this.tag = tag_text;
        this.timed_duration = time_text;
        this.short_description = sshort;
        this.long_description = llong;
        this.issueImage = issueImage;
        this.coordinate = coordinate;
    }

    public CommunityIssue(String sshort, String llong, String cat_text, String tag_text, String time_text) {
        this.category = cat_text;
        this.tag = tag_text;
        this.timed_duration = time_text;
        this.short_description = sshort;
        this.long_description = llong;
    }

    public CommunityIssue(String key) {
        this.firebaseID = key;
    }


    @Exclude
    public LatLng getCoordinate() {
        return coordinate;
    }

    @Exclude
    public void setCoordinate(LatLng coordinate) {
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


    @Exclude
    public int getIcon() {

        //final String category = getCategory();
        int iconresult = 0;
        switch (category) {
            case "Cleaning":
                iconresult = R.drawable.ic_cleaning_category;
                break;
            case "Work":
                iconresult = R.drawable.ic_work_category;
                break;
            case "Local":
                iconresult = R.drawable.ic_local_category;
                break;
            case "Trash":
                iconresult = R.drawable.ic_trash_category;
                break;
            case "Road":
                iconresult = R.drawable.ic_road_category;
                break;
        }
        return iconresult;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof CommunityIssue)) return false;
        CommunityIssue objissue = (CommunityIssue) obj;

        return this.firebaseID.equals(objissue.firebaseID);
    }

    @Override
    public int hashCode() {
        return firebaseID.hashCode();
    }
}
