package org.socialnet.socialnet.message.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationTypeDb;

import java.time.Instant;

public interface ConversationInfo {
    Long getConversationId();
    ConversationTypeDb getType();
    String getTitle();

    String getLastMessageContent();
    Instant getLastMessageTimestamp();
    String getLastMessageSenderId();
    String getLastMessageSenderFirstName();
    String getLastMessageSenderLastName();

    String getParticipantUserId();
    String getParticipantFirstName();
    String getParticipantLastName();
    String getParticipantAvatarUrl();
}