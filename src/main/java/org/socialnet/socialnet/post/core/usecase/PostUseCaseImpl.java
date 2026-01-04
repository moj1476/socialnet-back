package org.socialnet.socialnet.post.core.usecase;

import org.socialnet.socialnet.post.core.model.Post;
import org.socialnet.socialnet.post.core.model.PostSummary;
import org.socialnet.socialnet.post.core.model.TrendingTopic;
import org.socialnet.socialnet.post.core.port.input.PostUseCase;
import org.socialnet.socialnet.post.core.port.output.PostRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PostUseCaseImpl implements PostUseCase {

    private final PostRepository postRepository;

    public PostUseCaseImpl(PostRepository postRepository) {
        this.postRepository = Objects.requireNonNull(postRepository);
    }


    @Override
    public Post createPost(Post post) {
        return postRepository.createPost(post);
    }

    @Override
    public List<PostSummary> getPosts(String userId, String filterAuthorId, String filterType, int page, int size) {
        return postRepository.getPosts(userId, filterAuthorId, filterType, page, size);
    }

    @Override
    public void voteInPoll(String userId, Long postId, Long answerIndexes) {
        postRepository.voteInPoll(userId, postId, answerIndexes);
    }

    @Override
    public List<TrendingTopic> getTrendingTopics() {
        return postRepository.getTrendingTopics();
    }

    @Override
    public void removePost(String userId, Long postId) {
        postRepository.removePost(userId, postId);
    }
}
