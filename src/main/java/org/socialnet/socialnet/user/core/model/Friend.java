package org.socialnet.socialnet.user.core.model;

public record Friend(
        String id,
        String firstName,
        String lastName,
        String avatarUrl,
        String status,
        boolean isOnline,
        boolean isFriend
) { }