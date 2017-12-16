package com.dom.communityapp.storage;

import com.dom.communityapp.models.CommunityIssue;

/**
 * Used to implement observer pattern to listen for location changes in firebase
 */

public interface IssueLocationListener {

    void issueRemoved(CommunityIssue issue);

    void newIssue(CommunityIssue issue);

    void movedIssue(CommunityIssue issue);

    void onImageDownloaded(CommunityIssue issue);

}
