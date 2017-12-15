package com.dom.communityapp.models;


import android.content.Context;

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

    private IssueTag tag;

    private IssueTime time;

    private IssueCategory category;

    @Exclude
    private String firebaseID;

    //Default constructor required by firebase, just like the getters and setters for all the things
    public CommunityIssue() {

    }

    public CommunityIssue(String shortdescription, String longdescription, IssueCategory category, IssueTag tag, IssueTime issueTime, IssueImage issueImage, LatLng coordinate) {
        this.category = category;
        this.tag = tag;
        this.time = issueTime;
        this.short_description = shortdescription;
        this.long_description = longdescription;
        this.issueImage = issueImage;
        this.coordinate = coordinate;
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

    public IssueTag getTag() {
        return tag;
    }

    public void setTag(IssueTag tag) {
        this.tag = tag;
    }

    public IssueTime getTime() {
        return time;
    }

    public void setTime(IssueTime time) {
        this.time = time;
    }

    public IssueCategory getCategory() {

        return this.category;

    }

    public void setCategory(IssueCategory category) {
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
        switch (category.getIssueCategoryEnum()) {
            case CLEAN:
                iconresult = R.drawable.ic_cleaning_category;
                break;
            case MAINTANANCE:
                iconresult = R.drawable.ic_work_category;
                break;
            case BUILD:
                iconresult = R.drawable.ic_local_category;
                break;
            case TRASH:
                iconresult = R.drawable.ic_trash_category;
                break;
            case LOGISTIC:
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

    @Exclude
    public void attachTranslators(Context context) {
        IssueDropDownTranslator translator = new IssueDropDownTranslator(context);
        this.category.setTranslator(translator);
        this.tag.setTranslator(translator);
        this.time.setTranslator(translator);
    }
}
