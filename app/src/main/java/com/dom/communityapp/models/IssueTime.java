package com.dom.communityapp.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by daniel on 12/14/17.
 * Issue Time are serializable through Firebase.
 * Also returns Time as Arrays
 */

public class IssueTime implements Serializable {

    public enum Time {
        HOUR,
        EFTERNOON,
        WEEKEND,
        SHORTPROJECT,
        LONGPROJECT
    }

    private Time issueTimeEnum;

    @Exclude
    private transient IssueDropDownTranslator translator;

    @Exclude
    public void setTranslator(IssueDropDownTranslator translator) {
        this.translator = translator;
    }

    public IssueTime() {
    }

    public IssueTime(Time time, IssueDropDownTranslator translator) {
        this.issueTimeEnum = time;
        this.translator = translator;

    }

    public IssueTime(IssueDropDownTranslator translator) {
        this.translator = translator;
    }


    @Exclude
    public IssueTime[] generateTimeArray() {
        ArrayList<IssueTime> result = new ArrayList<>();
        for (Time category : Time.values()) {
            result.add(new IssueTime(category, translator));
        }

        IssueTime[] arrayres = new IssueTime[result.size()];
        return result.toArray(arrayres);
    }

    public Time getIssueTimeEnum() {
        return issueTimeEnum;
    }

    public void setIssueCategoryEnum(Time tag) {
        this.issueTimeEnum = tag;
    }

    @Exclude
    @Override
    public String toString() {
        return translator.transLateTimeToRessourceString(this.getIssueTimeEnum());
    }
}

