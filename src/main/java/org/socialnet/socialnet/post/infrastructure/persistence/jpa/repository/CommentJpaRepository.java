package org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    @Query(value = """
        SELECT 
            c.id AS id,
            c.post_id AS postId,
            c.content AS content,
            c.parent_comment_id AS parentId,
            c.created_at AS createdAt,
            
            u.id AS authorId,
            pr.first_name AS authorFirstName,
            pr.last_name AS authorLastName,
            pr.avatar_url AS authorAvatarUrl,
            
            p.author_id AS postAuthorId
        FROM comments c
        JOIN users u ON c.author_id = u.id
        LEFT JOIN profiles pr ON u.id = pr.id
        JOIN posts p ON c.post_id = p.id
        WHERE c.post_id = :postId AND c.is_deleted = false
        ORDER BY c.created_at ASC
    """, nativeQuery = true)
    List<CommentProjection> findAllByPostIdWithAuthor(@Param("postId") Long postId);

    interface CommentProjection {
        Long getId();
        Long getPostId();
        String getContent();
        Long getParentId();
        Instant getCreatedAt();
        
        String getAuthorId();
        String getAuthorFirstName();
        String getAuthorLastName();
        String getAuthorAvatarUrl();

        String getPostAuthorId();
    }
}