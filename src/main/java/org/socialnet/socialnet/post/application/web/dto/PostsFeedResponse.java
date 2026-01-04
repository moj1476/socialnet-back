package org.socialnet.socialnet.post.application.web.dto;

import java.time.Instant;

public record PostsFeedResponse(
        Long id,
        String content,
        String attachmentType,
        Object attachment,
        Instant createdAt,
        AuthorDto author,
        StatsDto stats,
        boolean isLikedByMe,
        boolean isRepostedByMe,
        boolean isFavoritedByMe
) {
    public record AuthorDto(
            String id,
            String firstName,
            String lastName,
            String avatarUrl
    ) {}

    public record StatsDto(
            long likes,
            long comments,
            long reposts
    ) {}
}