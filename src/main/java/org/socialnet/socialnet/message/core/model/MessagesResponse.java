package org.socialnet.socialnet.message.core.model;

import java.util.List;

public record MessagesResponse(
        List<Message> messages,
        long totalCount
) {
}
