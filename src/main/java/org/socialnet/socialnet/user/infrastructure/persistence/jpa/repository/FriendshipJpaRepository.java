package org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.FriendshipEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.FriendshipStatusDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface FriendshipJpaRepository extends JpaRepository<FriendshipEntity, Long> {

    @Query("""
        SELECT f FROM FriendshipEntity f 
        WHERE f.status = 'ACCEPTED' 
        AND (f.requesterId = :userId OR f.addresseeId = :userId)
    """)
    List<FriendshipEntity> findFriendsByUserId(@Param("userId") String userId);

    @Query("""
        SELECT f FROM FriendshipEntity f 
        WHERE (f.requesterId = :userId1 AND f.addresseeId = :userId2) 
           OR (f.requesterId = :userId2 AND f.addresseeId = :userId1)
    """)
    Optional<FriendshipEntity> findFriendshipBetween(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Query("SELECT f FROM FriendshipEntity f WHERE f.addresseeId = :userId AND f.status = 'PENDING'")
    List<FriendshipEntity> findIncomingFriendRequests(@Param("userId") String userId);

    Optional<FriendshipEntity> findByRequesterIdAndAddresseeIdAndStatus(
            String requesterId,
            String addresseeId,
            FriendshipStatusDb status
    );
    List<FriendshipEntity> findByRequesterIdAndStatus(String requesterId, FriendshipStatusDb status);

    @Query("SELECT COUNT(f) FROM FriendshipEntity f " +
            "WHERE (f.requesterId = :userId OR f.addresseeId = :userId) " +
            "AND f.status = 'ACCEPTED'")
    int countFriendsByUserId(@Param("userId") String userId);
}