package org.socialnet.socialnet.user.application.web.dto;

public record FriendResponse(
        String id,
        String firstName,
        String lastName,
        String avatarUrl,
        Boolean isOnline,
        Boolean isFriend
) { }
