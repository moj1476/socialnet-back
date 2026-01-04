package org.socialnet.socialnet.post.application.web.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long postId,
        String content,
        Long parentId,
        Instant createdAt,
        AuthorDto author
) {
    public record AuthorDto(
            String id,
            String firstName,
            String lastName,
            String avatarUrl
    ) {}
}