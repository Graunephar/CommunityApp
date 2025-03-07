package com.dom.communityapp.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by daniel on 12/14/17.
 * Issue Tags are serializable through Firebase.
 * Also returns tags as Arrays
 */

public class IssueTag implements Serializable {

    public enum Tag {
        ONEMANJOB,
        COOP,
        PROF
    }

    private Tag issueTagEnum;

    @Exclude
    private transient IssueDropDownTranslator translator;

    @Exclude
    public void setTranslator(IssueDropDownTranslator translator) {
        this.translator = translator;
    }


    public IssueTag() {
    }


    public IssueTag(Tag tag, IssueDropDownTranslator translator) {
        this.issueTagEnum = tag;
        this.translator = translator;

    }

    public IssueTag(IssueDropDownTranslator translator) {
        this.translator = translator;
    }


    @Exclude
    public IssueTag[] generateTagArray() {
        ArrayList<IssueTag> result = new ArrayList<>();
        for (Tag category : Tag.values()) {
            result.add(new IssueTag(category, translator));
        }

        IssueTag[] arrayres = new IssueTag[result.size()];
        return result.toArray(arrayres);
    }

    public Tag getIssueTagEnum() {
        return issueTagEnum;
    }

    public void setIssueCategoryEnum(Tag tag) {
        this.issueTagEnum = tag;
    }

    @Exclude
    @Override
    public String toString() {
        return translator.transLateTagToRessourceString(this.getIssueTagEnum());
    }
}

