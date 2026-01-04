package org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.ProfileEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, String> {
    @Query(value = """
        WITH user_interests AS (
            SELECT interests FROM profiles WHERE id = :userId
        )
        SELECT
            p.id as id,
            p.first_name as firstName,
            p.last_name as lastName,
            p.bio as bio,
            p.avatar_url as avatarUrl,
            p.city as city,
            p.birth_date as birthDate,
            p.work_or_study as workOrStudy,
            array_to_string(p.interests, ',') as interests, -- Изменено здесь
            p.created_at as createdAt,
            p.updated_at as updatedAt,
            (SELECT COUNT(*) FROM unnest(p.interests) AS interest
             WHERE interest IN (SELECT unnest(interests) FROM user_interests)) AS commonInterestsCount
        FROM
            profiles p, user_interests ui
        WHERE
            p.id <> :userId
            AND p.interests && ui.interests
            AND NOT EXISTS (
                            SELECT 1 FROM friendships f
                            WHERE
                                (f.requester_id = :userId AND f.addressee_id = p.id) OR
                                (f.requester_id = p.id AND f.addressee_id = :userId)
                        )
        ORDER BY
            commonInterestsCount DESC
    """, nativeQuery = true)
    List<ProfileJpaRepository.PossibleFriendProjection> findPotentialFriendsByInterests(@Param("userId") String userId, Pageable pageable);

    @Query("""
        SELECT p FROM ProfileEntity p WHERE p.id IN (
            SELECT f.addresseeId FROM FriendshipEntity f
            WHERE f.requesterId = :userId AND f.status = 'ACCEPTED'
            UNION
            SELECT f.requesterId FROM FriendshipEntity f
            WHERE f.addresseeId = :userId AND f.status = 'ACCEPTED'
        ) AND (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) 
           OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    List<ProfileEntity> findFriendsByName(
            @Param("userId") String userId,
            @Param("name") String name
    );


    @Query("""
        SELECT p FROM ProfileEntity p
        WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<ProfileEntity> searchProfilesByNameQuery(@Param("query") String query);

    interface PossibleFriendProjection {
        String getId();
        String getFirstName();
        String getLastName();
        String getBio();
        String getAvatarUrl();
        String getCity();
        LocalDate getBirthDate();
        String getWorkOrStudy();
        String getInterests();
        Instant getCreatedAt();
        Instant getUpdatedAt();
        Long getCommonInterestsCount();
    }
}