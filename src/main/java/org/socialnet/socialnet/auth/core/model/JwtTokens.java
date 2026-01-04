package org.socialnet.socialnet.auth.core.model;

public record JwtTokens (
        String accessToken,
        String refreshToken,
        int expiresIn
) {
}
