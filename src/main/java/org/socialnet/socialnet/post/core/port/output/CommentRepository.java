package org.socialnet.socialnet.post.core.port.output;

import org.socialnet.socialnet.post.core.model.Comment;

import java.util.List;

public interface CommentRepository {
    Comment addComment(Long postId, String userId, String content, Long parentCommentId);
    void deleteComment(Long commentId, String userId);
    List<Comment> getComments(Long postId);
}
