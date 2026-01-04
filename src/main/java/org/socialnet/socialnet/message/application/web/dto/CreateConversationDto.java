package org.socialnet.socialnet.message.application.web.dto;

import java.util.List;

public record CreateConversationDto(
    List<String> participantIds,
    String title,
    String message
) {
}
