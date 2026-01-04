package org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.RepostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepostJpaRepository extends JpaRepository<RepostEntity, Long> {
    
    boolean existsByUserIdAndPostIdAndIsPrivate(String userId, Long postId, boolean isPrivate);
    
    void deleteByUserIdAndPostIdAndIsPrivate(String userId, Long postId, boolean isPrivate);
    
    long countByPostId(Long postId);

    @Query(nativeQuery = true, value = """
        SELECT
            p.id AS id,
            p.id AS postId, -- Алиас для совместимости с proj.getPostId()
            p.content AS content,
            p.attachment_type AS attachmentType,
            p.created_at AS createdAt,
            
            -- Автор оригинального поста
            u.id AS authorId,
            pr.first_name AS authorFirstName,
            pr.last_name AS authorLastName,
            pr.avatar_url AS authorAvatarUrl,
            
            -- Счетчики
            (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.id) AS likesCount,
            (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id AND c.is_deleted = false) AS commentsCount,
            (SELECT COUNT(*) FROM reposts r_count WHERE r_count.post_id = p.id) AS repostsCount,
            
            -- Флаги (относительно viewerId, в данном случае userId)
            EXISTS(SELECT 1 FROM likes l WHERE l.post_id = p.id AND l.user_id = :userId) AS likedByMe,
            EXISTS(SELECT 1 FROM reposts r_chk WHERE r_chk.post_id = p.id AND r_chk.user_id = :userId AND r_chk.is_private = false) AS repostByMe,
            EXISTS(SELECT 1 FROM reposts r_fav WHERE r_fav.post_id = p.id AND r_fav.user_id = :userId AND r_fav.is_private = true) AS favoritedByMe,
            
             -- Голос в опросе
            (SELECT pv.option_id FROM post_attachments_poll_vote pv WHERE pv.poll_post_id = p.id AND pv.user_id = :userId) AS userVotedOptionId

        FROM reposts r
        JOIN posts p ON r.post_id = p.id
        JOIN users u ON p.author_id = u.id
        LEFT JOIN profiles pr ON u.id = pr.id
        WHERE r.user_id = :userId
        ORDER BY r.created_at DESC
    """)
    List<PostJpaRepository.PostSummaryProjection> findRepostsByUserId(@Param("userId") String userId);


    @Query(nativeQuery = true, value = """
        SELECT
            p.id AS id,
            p.id AS postId,
            p.content AS content,
            p.attachment_type AS attachmentType,
            p.created_at AS createdAt,
            
            u.id AS authorId,
            pr.first_name AS authorFirstName,
            pr.last_name AS authorLastName,
            pr.avatar_url AS authorAvatarUrl,
            
            (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.id) AS likesCount,
            (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id AND c.is_deleted = false) AS commentsCount,
            (SELECT COUNT(*) FROM reposts r_count WHERE r_count.post_id = p.id) AS repostsCount,
            
            EXISTS(SELECT 1 FROM likes l WHERE l.post_id = p.id AND l.user_id = :userId) AS likedByMe,
            EXISTS(SELECT 1 FROM reposts r_chk WHERE r_chk.post_id = p.id AND r_chk.user_id = :userId AND r_chk.is_private = false) AS repostByMe,
            EXISTS(SELECT 1 FROM reposts r_fav WHERE r_fav.post_id = p.id AND r_fav.user_id = :userId AND r_fav.is_private = true) AS favoritedByMe,
            
            (SELECT pv.option_id FROM post_attachments_poll_vote pv WHERE pv.poll_post_id = p.id AND pv.user_id = :userId) AS userVotedOptionId

        FROM reposts r
        JOIN posts p ON r.post_id = p.id
        JOIN users u ON p.author_id = u.id
        LEFT JOIN profiles pr ON u.id = pr.id
        WHERE r.user_id = :userId
          AND r.is_private = :isPrivate
        ORDER BY r.created_at DESC
    """)
    List<PostJpaRepository.PostSummaryProjection> findRepostsByUserIdAndIsPrivate(
            @Param("userId") String userId,
            @Param("isPrivate") boolean isPrivate
    );
}