package org.socialnet.socialnet.post.core.usecase;

import org.socialnet.socialnet.post.core.port.input.LikeUseCase;
import org.socialnet.socialnet.post.core.port.output.LikeRepository;

public class LikeUseCaseImpl implements LikeUseCase {
    private final LikeRepository likeRepository;

    public LikeUseCaseImpl(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public void likePost(Long postId, String userId) {
        likeRepository.likePost(postId, userId);
    }

    @Override
    public void unlikePost(Long postId, String userId) {
        likeRepository.unlikePost(postId, userId);
    }
}
