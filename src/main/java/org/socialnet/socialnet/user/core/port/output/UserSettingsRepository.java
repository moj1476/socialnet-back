package org.socialnet.socialnet.user.core.port.output;

import org.socialnet.socialnet.user.core.model.SettingsEntity;

import java.util.Optional;

public interface UserSettingsRepository {
    Optional<SettingsEntity> findByUserId(String userId);
    SettingsEntity save(SettingsEntity settings);
}