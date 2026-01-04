package org.socialnet.socialnet.post.core.port.output;

public interface LikeRepository {
    void likePost(Long postId, String userId);
    void unlikePost(Long postId, String userId);
}
