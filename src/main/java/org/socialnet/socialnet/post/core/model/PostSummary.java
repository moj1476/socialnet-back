package org.socialnet.socialnet.post.core.model;

import java.time.Instant;
import java.util.List;

public record PostSummary(
        Long id,
        String content,
        Post.Attachment attachment,
        String attachmentType,
        Instant createdAt,
        
        AuthorInfo author,
        
        long likesCount,
        long commentsCount,
        long repostsCount,
        
        boolean isLikedByMe,
        boolean isRepostedByMe,
        boolean isFavoritedByMe,

        Long userVotedOptionId
) {
    public record AuthorInfo(
            String id,
            String firstName,
            String lastName,
            String avatarUrl
    ) {}
}