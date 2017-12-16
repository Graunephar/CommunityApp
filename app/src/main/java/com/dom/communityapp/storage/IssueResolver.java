package com.dom.communityapp.storage;

import com.dom.communityapp.models.CommunityIssue;

/**
 * Used as callback when issue is resolved
 */

public interface IssueResolver {
    void resolve(CommunityIssue issue);
}
