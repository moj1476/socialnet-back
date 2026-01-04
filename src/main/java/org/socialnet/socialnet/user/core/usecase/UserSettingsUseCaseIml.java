package org.socialnet.socialnet.user.core.usecase;

import org.socialnet.socialnet.user.core.model.SettingsEntity;
import org.socialnet.socialnet.user.core.port.input.UserSettingsUseCase;
import org.socialnet.socialnet.user.core.port.output.UserSettingsRepository;

import java.util.Optional;

public class UserSettingsUseCaseIml implements UserSettingsUseCase {
    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsUseCaseIml(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    @Override
    public Optional<SettingsEntity> findByUserId(String userId) {
        return userSettingsRepository.findByUserId(userId);
    }

    @Override
    public SettingsEntity save(SettingsEntity settings) {
        return userSettingsRepository.save(settings);
    }
}
