package org.socialnet.socialnet.post.core.port.output;

import org.socialnet.socialnet.post.core.model.PostSummary;

import java.util.List;

public interface RepostRepository {
    void createRepost(String userId, Long postId, boolean isPrivate);
    void deleteRepost(String userId, Long postId, boolean isPrivate);
    long getRepostsCount(Long postId);
    List<PostSummary> getReposts(String userId, Boolean isPrivate);
}