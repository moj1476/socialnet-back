package org.socialnet.socialnet.post.application.web.dto;

import java.time.Instant;

public record PostsSummaryResponse(
        Long id,
        Long authorId,
        String contentSnippet,
        String attachmentType,
        Instant createdAt
) {}
