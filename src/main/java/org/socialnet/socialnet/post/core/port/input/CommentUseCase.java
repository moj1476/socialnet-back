package org.socialnet.socialnet.post.core.port.input;

import org.socialnet.socialnet.post.core.model.Comment;
import java.util.List;

public interface CommentUseCase {
    Comment addComment(Long postId, String userId, String content, Long parentId);
    void deleteComment(Long commentId, String userId);
    List<Comment> getComments(Long postId);
}