package org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeJpaRepository extends JpaRepository<LikeEntity, Long> {
    boolean existsByUserIdAndPostId(String userId, Long postId);
    void deleteByUserIdAndPostId(String userId, Long postId);
    int countByPostId(Long postId);

    @Query(value = """
        SELECT COUNT(l.id)
        FROM likes l
        JOIN posts p ON l.post_id = p.id
        WHERE p.author_id = :userId
          AND p.is_deleted = false
    """, nativeQuery = true)
    int countLikesReceivedByUser(@Param("userId") String userId);
}