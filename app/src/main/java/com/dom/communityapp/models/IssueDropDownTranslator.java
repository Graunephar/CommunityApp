package com.dom.communityapp.models;

import android.app.Activity;
import android.content.Context;

import com.dom.communityapp.R;
import com.google.firebase.database.Exclude;

import java.util.Locale;

/**
 * Created by daniel on 12/14/17.
 */

public class IssueDropDownTranslator {


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


}
