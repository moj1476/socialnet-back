package org.socialnet.socialnet.user.application.web.dto;

public record OnboardingParams(
        int onboardingStep,
        boolean isOnboardingCompleted
) {
}
