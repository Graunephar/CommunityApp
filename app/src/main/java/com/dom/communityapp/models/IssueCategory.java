package com.dom.communityapp.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

/**
 * Created by daniel on 12/14/17.
 */

public class IssueCategory {

    @Exclude
    public void setTranslator(IssueDropDownTranslator translator) {
        this.translator = translator;
    }

    public enum Category {
        BUILD,
        CLEAN,
        MAINTANANCE,
        TRASH,
        LOGISTIC
    }

    private Category issueCategoryEnum;

    @Exclude
    private IssueDropDownTranslator translator;

    public IssueCategory() {
    }


    public IssueCategory(Category category, IssueDropDownTranslator translator) {
        this.issueCategoryEnum = category;
        this.translator = translator;

    }

    public IssueCategory(IssueDropDownTranslator translator) {
        this.translator = translator;
    }

    @Exclude
    public IssueCategory[] generateCatArray() {
        ArrayList<IssueCategory> result = new ArrayList<>();
        for(Category category : Category.values()) {
            result.add(new IssueCategory(category, translator));
        }

        IssueCategory[] catres = new IssueCategory[result.size()];
        return result.toArray(catres);
    }

    public Category getIssueCategoryEnum() {
        return issueCategoryEnum;
    }

    public void setIssueCategoryEnum(Category issueCategoryEnum) {
        this.issueCategoryEnum = issueCategoryEnum;
    }

    @Exclude
    @Override
    public String toString() {
        return translator.transLateCategoryToRessourceString(this.getIssueCategoryEnum());
    }
}

