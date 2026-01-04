package org.socialnet.socialnet.post.core.usecase;

import org.socialnet.socialnet.post.core.model.PostSummary;
import org.socialnet.socialnet.post.core.port.input.RepostUseCase;
import org.socialnet.socialnet.post.core.port.output.RepostRepository;

import java.util.List;

public class RepostUseCaseImpl implements RepostUseCase {
    private final RepostRepository repo;

    public RepostUseCaseImpl(RepostRepository repo) {
        this.repo = repo;
    }


    @Override
    public void repostPost(String userId, Long postId, boolean isPrivate) {
        repo.createRepost(userId, postId, isPrivate);
    }

    @Override
    public void removeRepost(String userId, Long postId, boolean isPrivate) {
        repo.deleteRepost(userId, postId, isPrivate);
    }

    @Override
    public List<PostSummary> getReposts(String userId, Boolean isPrivate) {
        return repo.getReposts(userId, isPrivate);
    }
}
