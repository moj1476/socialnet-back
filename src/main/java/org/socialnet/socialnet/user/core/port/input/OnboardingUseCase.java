package org.socialnet.socialnet.user.core.port.input;

public interface OnboardingUseCase {
    void updateUserOnboardingStep(UpdateOnboardingStepCommand command);
    GetOnboardingStepResponse getUserOnboardingStep(String userId);

    record UpdateOnboardingStepCommand(
            String userId,
            int onboardingStep
    ) {}

    record GetOnboardingStepResponse(
            int onboardingStep,
            boolean isOnboardingCompleted
    ) {}
}
