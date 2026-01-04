package org.socialnet.socialnet.post.core.model;

import java.time.Instant;

public record Comment(
        Long id,
        Long postId,
        String content,
        Long parentId,
        Instant createdAt,
        
        AuthorInfo author,

        String postAuthorId
) {
    public record AuthorInfo(
            String id,
            String firstName,
            String lastName,
            String avatarUrl
    ) {}
}