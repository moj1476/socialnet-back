package org.socialnet.socialnet.post.core.port.input;

import org.socialnet.socialnet.post.core.model.PostSummary;

import java.util.List;

public interface RepostUseCase {
    void repostPost(String userId, Long postId, boolean isPrivate);
    void removeRepost(String userId, Long postId, boolean isPrivate);
    List<PostSummary> getReposts(String userId, Boolean isPrivate);
}