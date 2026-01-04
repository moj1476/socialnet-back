package org.socialnet.socialnet.message.application.web.dto;

public record CallSignalResponse(
        String type,
        String title,
        String callerName,
        String callerId,
        String callerAvatarUrl,
        String chatId
) {
}
