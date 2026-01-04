package org.socialnet.socialnet.post.core.usecase;

import org.socialnet.socialnet.post.core.model.Comment;
import org.socialnet.socialnet.post.core.port.input.CommentUseCase;
import org.socialnet.socialnet.post.core.port.output.CommentRepository;

import java.util.List;

public class CommentUseCaseImpl implements CommentUseCase {
    private final CommentRepository commentRepository;

    public CommentUseCaseImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment addComment(Long postId, String userId, String content, Long parentId) {
        return commentRepository.addComment(postId, userId, content, parentId);
    }

    @Override
    public void deleteComment(Long commentId, String userId) {
        commentRepository.deleteComment(commentId, userId);
    }

    @Override
    public List<Comment> getComments(Long postId) {
        return commentRepository.getComments(postId);
    }
}
