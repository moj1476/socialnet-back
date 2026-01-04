package org.socialnet.socialnet.message.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {
    Page<MessageEntity> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

    @Query("""
        SELECT m
        FROM MessageEntity m
        JOIN m.conversation c
        JOIN c.participants p
        WHERE c.id = :conversationId
          AND p.userId = :userId
        ORDER BY m.createdAt DESC
    """)
    Page<MessageEntity> findByConversationIdAndUserId(
            @Param("conversationId") Long conversationId,
            @Param("userId") String userId,
            Pageable pageable
    );

    @Query(value = """
        SELECT * FROM (
            (SELECT * FROM messages m 
             WHERE m.conversation_id = :chatId 
               AND m.created_at <= (SELECT created_at FROM messages WHERE id = :msgId)
             ORDER BY m.created_at DESC 
             LIMIT 20)
            UNION
            (SELECT * FROM messages m 
             WHERE m.conversation_id = :chatId 
               AND m.created_at > (SELECT created_at FROM messages WHERE id = :msgId)
             ORDER BY m.created_at ASC 
             LIMIT 20)
        ) as combined_messages
        ORDER BY created_at ASC
    """, nativeQuery = true)
    List<MessageEntity> findMessagesAroundId(
            @Param("chatId") Long conversationId,
            @Param("msgId") Long messageId
    );

    @Query("""
        SELECT m FROM MessageEntity m
        JOIN m.conversation c
        JOIN c.participants p
        WHERE c.id = :conversationId
          AND p.userId = :userId
          AND LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY m.createdAt DESC
    """)
    List<MessageEntity> searchByText(
            @Param("conversationId") Long conversationId,
            @Param("userId") String userId,
            @Param("query") String query
    );

    @Query("""
        SELECT m FROM MessageEntity m
        WHERE m.conversation.id = :conversationId
          AND m.createdAt < :date
        ORDER BY m.createdAt DESC
    """)
    List<MessageEntity> findBefore(
            @Param("conversationId") Long conversationId,
            @Param("date") Instant date,
            Pageable pageable
    );

    @Query("""
        SELECT m FROM MessageEntity m
        WHERE m.conversation.id = :conversationId
          AND m.createdAt > :date
        ORDER BY m.createdAt ASC
    """)
    List<MessageEntity> findAfter(
            @Param("conversationId") Long conversationId,
            @Param("date") Instant date,
            Pageable pageable
    );
}