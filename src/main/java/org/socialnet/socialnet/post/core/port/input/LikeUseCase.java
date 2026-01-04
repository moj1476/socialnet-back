package org.socialnet.socialnet.post.core.port.input;

public interface LikeUseCase {
    void likePost(Long postId, String userId);
    void unlikePost(Long postId, String userId);
}