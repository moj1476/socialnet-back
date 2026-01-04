package org.socialnet.socialnet.user.application.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record DecodedTokenResponse (
    @NotNull
    String userId,

    @NotNull
    String email,

    @NotNull
    String login,

    @NotNull
    OnboardingParams onboarding,

    Optional<ProfileResponse> profile
) {

}
