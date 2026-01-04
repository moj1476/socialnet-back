package org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.post.core.model.PostSummary;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {
    int countByAuthorId(String authorId);

    @Query(value = """
        SELECT
            p.id AS id,
            p.content AS content,
            p.attachment_type AS attachmentType,
            p.created_at AS createdAt,
            
            -- Данные автора (JOIN с profiles)
            u.id AS authorId,
            pr.first_name AS authorFirstName,
            pr.last_name AS authorLastName,
            pr.avatar_url AS authorAvatarUrl,
            
            -- Подсчет лайков (подзапрос или join)
            (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.id) AS likesCount,
            
            -- Подсчет комментариев
            (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id AND c.is_deleted = false) AS commentsCount,
            
            -- Проверка, лайкнул ли текущий пользователь (viewerId)
            EXISTS(SELECT 1 FROM likes l WHERE l.post_id = p.id AND l.user_id = :viewerId) AS likedByMe, 
            
            -- Проверка, сделал ли репост текущий пользователь (viewerId)
            EXISTS(SELECT 1 FROM reposts r WHERE r.post_id = p.id AND r.user_id = :viewerId AND r.is_private = false) AS repostByMe,
            
            -- Подсчет репостов
            (SELECT COUNT(*) FROM reposts r WHERE r.post_id = p.id) AS repostsCount,
            
            -- Проверка, добавил ли текущий пользователь в избранное (viewerId)
            EXISTS(SELECT 1 FROM reposts r WHERE r.post_id = p.id AND r.user_id = :viewerId AND r.is_private = true) AS favoritedByMe,
            
            (SELECT pv.option_id FROM post_attachments_poll_vote pv WHERE pv.poll_post_id = p.id AND pv.user_id = :viewerId) AS userVotedOptionId
            
        FROM posts p
        JOIN users u ON p.author_id = u.id
        LEFT JOIN profiles pr ON u.id = pr.id
        WHERE p.is_deleted = false
          AND (:authorId IS NULL OR p.author_id = :authorId) -- Фильтр по автору (опционально)
          AND (:type IS NULL OR p.attachment_type = CAST(:type AS post_attachment_type)) -- Фильтр по типу (опционально)
        ORDER BY p.created_at DESC
    """, nativeQuery = true)
    List<PostSummaryProjection> findPostsWithStats(
            @Param("viewerId") String viewerId,
            @Param("authorId") String authorId,
            @Param("type") String type,
            Pageable pageable
    );

    @Query(value = """
        SELECT lower(matches[1]) as tag, COUNT(*) as count
        FROM posts,
             regexp_matches(content, '#([а-яА-Яa-zA-Z0-9_]+)', 'g') as matches
        WHERE is_deleted = false
        GROUP BY tag
        ORDER BY count DESC
        LIMIT 4
    """, nativeQuery = true)
    List<Object[]> findTrendingTagsRaw();

    interface PostSummaryProjection {
        Long getId();
        String getContent();
        String getAttachmentType();
        Instant getCreatedAt();

        String getAuthorId();
        String getAuthorFirstName();
        String getAuthorLastName();
        String getAuthorAvatarUrl();

        long getLikesCount();
        long getCommentsCount();
        boolean getLikedByMe();
        boolean getRepostByMe();
        long getRepostsCount();
        boolean getFavoritedByMe();

        Long getUserVotedOptionId();
    }

}