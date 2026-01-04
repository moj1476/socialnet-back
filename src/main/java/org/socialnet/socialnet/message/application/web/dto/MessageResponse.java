package org.socialnet.socialnet.message.application.web.dto;

import java.util.List;

public record MessageResponse(
        List<MessageDto> messages,
        long totalCount
) {
}
