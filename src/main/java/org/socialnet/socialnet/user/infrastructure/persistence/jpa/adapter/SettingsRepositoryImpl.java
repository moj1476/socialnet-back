package org.socialnet.socialnet.user.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.user.core.model.SettingsEntity;
import org.socialnet.socialnet.user.core.port.output.UserSettingsRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserSettingsEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.mapper.SettingsPersistenceMapper;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.UserSettingsJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SettingsRepositoryImpl implements UserSettingsRepository {

    private final UserSettingsJpaRepository userSettingsJpaRepository;
    private final SettingsPersistenceMapper settingsPersistenceMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<SettingsEntity> findByUserId(String userId) {
        return userSettingsJpaRepository.findById(userId)
                .map(settingsPersistenceMapper::toDomain);
    }

    @Override
    @Transactional
    public SettingsEntity save(SettingsEntity settings) {
        UserSettingsEntity entityToSave = settingsPersistenceMapper.toEntity(settings);

        UserSettingsEntity savedEntity = userSettingsJpaRepository.save(entityToSave);

        return settingsPersistenceMapper.toDomain(savedEntity);
    }
}