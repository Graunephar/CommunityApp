package com.dom.communityapp.models;

import android.app.Activity;
import android.content.Context;

import com.dom.communityapp.R;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by daniel on 12/14/17.
 */

public class IssueDropDownTranslator{


    private final Context mContext;

    public IssueDropDownTranslator(Context context) {
        this.mContext = context;
    }

    public String transLateCategoryToRessourceString(IssueCategory.Category category) {
        switch (category) {
            case MAINTANANCE: return mContext.getString(R.string.cat_maintenance);
            case CLEAN: return mContext.getString(R.string.cat_clean);
            case BUILD: return mContext.getString(R.string.cat_build);
            case LOGISTIC: return mContext.getString(R.string.cat_log);
            case TRASH: return mContext.getString(R.string.cat_trash);
        }
        return null;
    }


    public String transLateTagToRessourceString(IssueTag.Tag tag) {
        switch (tag) {
            case COOP: return mContext.getString(R.string.tags_coop);
            case PROF: return mContext.getString(R.string.tags_pro);
            case ONEMANJOB: return mContext.getString(R.string.tags_job);
        }
        return null;
    }

    public String transLateTimeToRessourceString(IssueTime.Time time) {
        switch (time) {
            case HOUR: return mContext.getString(R.string.time_hour);
            case EFTERNOON: return mContext.getString(R.string.time_afternoon);
            case WEEKEND: return mContext.getString(R.string.time_weekend);
            case LONGPROJECT: return mContext.getString(R.string.time_longproject);
            case SHORTPROJECT: return mContext.getString(R.string.time_shortproject);
        }
        return null;
    }
}
