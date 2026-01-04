package org.socialnet.socialnet.user.core.port.input;

import org.socialnet.socialnet.user.core.model.SettingsEntity;

import java.util.Optional;

public interface UserSettingsUseCase {
    Optional<SettingsEntity> findByUserId(String userId);
    SettingsEntity save(SettingsEntity settings);
}
