package com.dom.communityapp.storage;

import android.net.Uri;

import com.dom.communityapp.models.CommunityIssue;

/**
 * Created by daniel on 12/8/17.
 */

public interface FirebaseObserver {

    void onDataChanged(String value);
    void getImage(Uri downloadUrl);
    void onNewIssue(CommunityIssue issue);
    void imageDownloaded(CommunityIssue issue);
}
