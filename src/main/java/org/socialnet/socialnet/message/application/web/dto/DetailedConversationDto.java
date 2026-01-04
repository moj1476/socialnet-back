package org.socialnet.socialnet.message.application.web.dto;

import java.util.List;

public record DetailedConversationDto(
        Long id,
        String title,
        String createdAt,
        List<Message> messages,
        List<Participant> participants
) {
    public record Message(
            Long id,
            String senderId,
            String senderName,
            String content,
            String type,
            String createdAt
    ) { }
    public record Participant(
            String userId,
            String userName
    ) { }
}
