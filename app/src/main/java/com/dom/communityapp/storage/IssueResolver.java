package com.dom.communityapp.storage;

import com.dom.communityapp.models.CommunityIssue;

/**
 * Created by daniel on 12/15/17.
 */

public interface IssueResolver {
    void resolve(CommunityIssue issue);
}
