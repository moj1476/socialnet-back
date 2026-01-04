package org.socialnet.socialnet.post.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.socialnet.socialnet.post.core.model.Comment;
import org.socialnet.socialnet.post.core.port.output.CommentRepository;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.CommentEntity;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.CommentJpaRepository;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.PostJpaRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.ProfileJpaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    private final CommentJpaRepository commentJpaRepository;
    private final ProfileJpaRepository profileJpaRepository;
    private final PostJpaRepository postJpaRepository;

    @Override
    @Transactional
    @CacheEvict(value = "postComments", key = "#postId")
    public Comment addComment(Long postId, String userId, String content, Long parentCommentId) {
        var post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        String postAuthorId = post.getAuthorId();
        var commentEntity = new CommentEntity();
        commentEntity.setPostId(postId);
        commentEntity.setAuthorId(userId);
        commentEntity.setContent(content);
        commentEntity.setParentCommentId(parentCommentId);

        var saved = commentJpaRepository.save(commentEntity);

        var profile = profileJpaRepository.findById(userId).orElse(null);

        return mapToDomain(saved, profile != null ? profile.getFirstName() : "User",
                profile != null ? profile.getLastName() : "",
                profile != null ? profile.getAvatarUrl() : null, postAuthorId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "postComments", allEntries = true)
    public void deleteComment(Long commentId, String userId) {
        var comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        if (!comment.getAuthorId().equals(userId)) {
            log.warn("User {} tried to delete comment {} owned by {}", userId, commentId, comment.getAuthorId());
            throw new RuntimeException("У вас нет прав на удаление этого комментария");
        }

        comment.setDeleted(true);
        commentJpaRepository.save(comment);

        log.info("Comment {} soft deleted by user {}", commentId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "postComments", key = "#postId")
    public List<Comment> getComments(Long postId) {
        return commentJpaRepository.findAllByPostIdWithAuthor(postId)
                .stream()
                .map(p -> new Comment(
                        p.getId(),
                        p.getPostId(),
                        p.getContent(),
                        p.getParentId(),
                        p.getCreatedAt(),
                        new Comment.AuthorInfo(
                                p.getAuthorId(),
                                p.getAuthorFirstName(),
                                p.getAuthorLastName(),
                                p.getAuthorAvatarUrl()
                        ),
                        p.getPostAuthorId()
                ))
                .toList();
    }

    private Comment mapToDomain(CommentEntity entity, String firstName, String lastName, String avatarUrl, String postAuthorId) {
        return new Comment(
                entity.getId(),
                entity.getPostId(),
                entity.getContent(),
                entity.getParentCommentId(),
                entity.getCreatedAt(),
                new Comment.AuthorInfo(
                        entity.getAuthorId(),
                        firstName,
                        lastName,
                        avatarUrl
                ),
                postAuthorId
        );
    }
}