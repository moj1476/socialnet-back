package org.socialnet.socialnet.user.core.port.output;

import org.socialnet.socialnet.user.core.model.User;

public interface UserRepository {
    User findOrCreate(String userId, String login, String email);
    User findById(String userId);
    void updateUserOnboardingStep(String userId, int onboardingStep);
    int getUserOnboardingStep(String userId);
}
