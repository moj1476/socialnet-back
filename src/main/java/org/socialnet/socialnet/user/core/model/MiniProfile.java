package org.socialnet.socialnet.user.core.model;

public record MiniProfile(
        String id,
        String firstName,
        String lastName,
        String avatarUrl,
        Boolean isOnline
) {
}
