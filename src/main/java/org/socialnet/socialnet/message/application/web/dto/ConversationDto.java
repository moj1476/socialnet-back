package org.socialnet.socialnet.message.application.web.dto;

public record ConversationDto(
        Long id,
        String title,
        String avatarUrl,
        String lastMessage,
        boolean isOnline
) {
}
