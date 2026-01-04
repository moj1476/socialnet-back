package org.socialnet.socialnet.message.core.model;

import java.util.List;

public record Call(
        List<Participant> participants,
        String title,
        String callerName,
        String callerId,
        String callerAvatarUrl
) {
    public record Participant(
            String userId,
            String username,
            String avatarUrl
    ) {
    }
}
