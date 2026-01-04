package org.socialnet.socialnet.user.application.web.dto;

import java.util.Optional;

public record OnboardingResponse(
        OnboardingParams onboarding,
        Optional<ProfileResponse> profile
) { }
