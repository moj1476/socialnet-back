package org.socialnet.socialnet.auth.application.web.dto;

import jakarta.validation.constraints.NotNull;

public record TokenResponse(
        @NotNull
        String accessToken,

        @NotNull
        String refreshToken,

        @NotNull
        int expiresIn
) {
}
