package org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsJpaRepository extends JpaRepository<UserSettingsEntity, String> {
}
