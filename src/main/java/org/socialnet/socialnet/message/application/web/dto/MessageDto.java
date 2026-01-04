package org.socialnet.socialnet.message.application.web.dto;

import java.time.Instant;

public record MessageDto(
        Long id,
        Long conversationId,
        String senderId,
        String content,
        String type,
        // boolean isRead,
        Instant createdAt
) {
}