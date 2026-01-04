package org.socialnet.socialnet.post.application.web.dto;

public record VoteRequest(
    Long postId,
    Long variant
) {
}
