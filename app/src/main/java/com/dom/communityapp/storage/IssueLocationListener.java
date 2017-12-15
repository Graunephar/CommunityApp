package com.dom.communityapp.storage;

import com.dom.communityapp.models.CommunityIssue;

/**
 * Created by mrl on 13/12/2017.
 */

public interface IssueLocationListener {
    void issueRemoved(CommunityIssue issue);

    void newIssue(CommunityIssue issue);

    void movedIssue(CommunityIssue issue);

    void onImageDownloaded(CommunityIssue issue);

}
