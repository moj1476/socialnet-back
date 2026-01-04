package org.socialnet.socialnet.user.application.web.dto;

public record PossibleFriendsResponse(
        String id,
        String firstName,
        String lastName,
        String avatarUrl,
        String city,
        int commonInterestsCount,
        boolean isOnline
) {
}
