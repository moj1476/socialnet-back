package org.socialnet.socialnet.post.core.port.input;

import org.socialnet.socialnet.post.core.model.Post;
import org.socialnet.socialnet.post.core.model.PostSummary;
import org.socialnet.socialnet.post.core.model.TrendingTopic;

import java.util.List;

public interface PostUseCase {
    Post createPost(Post post);
    List<PostSummary> getPosts(String userId, String filterAuthorId, String filterType, int page, int size);
    void voteInPoll(String userId, Long postId, Long answerIndexes);
    List<TrendingTopic> getTrendingTopics();
    void removePost(String userId, Long postId);
}
