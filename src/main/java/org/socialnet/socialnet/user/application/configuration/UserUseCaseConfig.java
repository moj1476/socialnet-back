package org.socialnet.socialnet.user.application.configuration;

import org.socialnet.socialnet.shared.core.port.output.FileStoragePort;
import org.socialnet.socialnet.user.core.port.input.*;
import org.socialnet.socialnet.user.core.port.output.ProfileRepository;
import org.socialnet.socialnet.user.core.port.output.UserRepository;
import org.socialnet.socialnet.user.core.port.output.UserSettingsRepository;
import org.socialnet.socialnet.user.core.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfig {

    @Bean
    public ChangeAvatarUseCase changeAvatar(ProfileRepository profileRepository,
                                            UserRepository userRepository,
                                            FileStoragePort fileStoragePort) {
        return new ChangeAvatarUseCaseImpl(profileRepository, userRepository, fileStoragePort);
    }


    @Bean
    public GetCurrentUserUseCase getCurrentUser(UserRepository userRepository) {
        return new GetCurrentUserUseCaseImpl(userRepository);
    }

    @Bean
    public OnboardingUseCase onboarding(UserRepository userRepository) {
        return new OnboardingUseCaseImpl(userRepository);
    }

    @Bean
    public ProfileUseCase profile(ProfileRepository profileRepository) {
        return new ProfileUseCaseImpl(profileRepository);
    }

    @Bean
    public UserSettingsUseCase userSettingsUseCase(UserSettingsRepository userRepository) {
        return new UserSettingsUseCaseIml(userRepository);
    }
}
