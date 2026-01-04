package org.socialnet.socialnet.user.application.web.dto;

public record MiniProfileResponse(
        String id,
        String firstName,
        String lastName,
        String avatarUrl,
        Boolean isOnline
) {
}
