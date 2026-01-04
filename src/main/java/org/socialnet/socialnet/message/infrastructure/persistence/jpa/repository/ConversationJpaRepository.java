package org.socialnet.socialnet.message.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationJpaRepository extends JpaRepository<ConversationEntity, Long> {
    @Query("""
        SELECT c FROM ConversationEntity c
        WHERE c.type = 'PRIVATE'
        AND (SELECT count(p) FROM c.participants p) = 2
        AND EXISTS (SELECT p FROM c.participants p WHERE p.userId = :userId1)
        AND EXISTS (SELECT p FROM c.participants p WHERE p.userId = :userId2)
    """)
    Optional<ConversationEntity> findPrivateConversationByTwoUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    Boolean existsByIdAndParticipantsUserId(Long conversationId, String userId);

    List<ConversationEntity> findAllByParticipantsUserId(String userId);

    @Query("""
    SELECT c FROM ConversationEntity c
    LEFT JOIN FETCH c.participants p
    WHERE c.id = :conversationId AND EXISTS (
        SELECT 1 FROM c.participants p_check WHERE p_check.userId = :userId
    )
""")
    Optional<ConversationEntity> findConversationWithParticipants(@Param("conversationId") Long conversationId, @Param("userId") String userId);

    @Query("""
    SELECT c FROM ConversationEntity c
    LEFT JOIN FETCH c.messages m
    WHERE c.id = :conversationId
    ORDER BY m.createdAt ASC
""")
    Optional<ConversationEntity> findConversationWithMessages(@Param("conversationId") Long conversationId);

    @Query("""
        SELECT c FROM ConversationEntity c
        JOIN c.participants p1
        JOIN c.participants p2
        WHERE c.type = 'PRIVATE'
          AND p1.userId = :userId1
          AND p2.userId = :userId2
    """)
    Optional<ConversationEntity> findPrivateConversationBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Query(value = """
        WITH last_messages AS (
            SELECT
                conversation_id,
                content,
                created_at,
                sender_id, 
                ROW_NUMBER() OVER(PARTITION BY conversation_id ORDER BY created_at DESC) as rn
            FROM messages
        ),
        other_participants AS (
            SELECT
                p.conversation_id,
                p.user_id,
                pr.first_name, 
                pr.last_name,  
                pr.avatar_url
            FROM conversation_participants p
            LEFT JOIN profiles pr ON p.user_id = pr.id
            WHERE p.user_id != :currentUserId
        )
        SELECT
            c.id AS conversationId,
            c.type AS type,
            c.title AS title,
            lm.content AS lastMessageContent,
            lm.created_at AS lastMessageTimestamp,
            op.user_id AS participantUserId,
            op.first_name AS participantFirstName, 
            op.last_name AS participantLastName,   
            op.avatar_url AS participantAvatarUrl,
            lm.sender_id AS lastMessageSenderId,  
            sender_profile.first_name AS lastMessageSenderFirstName, 
            sender_profile.last_name AS lastMessageSenderLastName  
        FROM conversations c
        JOIN conversation_participants cp ON c.id = cp.conversation_id
        LEFT JOIN last_messages lm ON c.id = lm.conversation_id AND lm.rn = 1
        LEFT JOIN other_participants op ON c.id = op.conversation_id AND c.type = 'PRIVATE'
        LEFT JOIN profiles sender_profile ON lm.sender_id = sender_profile.id
        WHERE cp.user_id = :currentUserId
        ORDER BY lm.created_at DESC NULLS LAST, c.id DESC;
    """, nativeQuery = true)
    List<ConversationInfo> findConversationsForUser(@Param("currentUserId") String currentUserId);
}