package org.socialnet.socialnet.user.core.usecase;

import org.socialnet.socialnet.user.core.port.input.OnboardingUseCase;
import org.socialnet.socialnet.user.core.port.output.UserRepository;

public class OnboardingUseCaseImpl implements OnboardingUseCase {
    private final UserRepository userRepository;

    private final int MAX_ONBOARDING_STEP = 5;

    public OnboardingUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void updateUserOnboardingStep(UpdateOnboardingStepCommand command) {
        userRepository.updateUserOnboardingStep(command.userId(), command.onboardingStep());
    }

    @Override
    public GetOnboardingStepResponse getUserOnboardingStep(String userId) {
        int step = userRepository.getUserOnboardingStep(userId);
        boolean isCompleted = step >= MAX_ONBOARDING_STEP;

        return new GetOnboardingStepResponse(
                step,
                isCompleted
        );
    }
}
