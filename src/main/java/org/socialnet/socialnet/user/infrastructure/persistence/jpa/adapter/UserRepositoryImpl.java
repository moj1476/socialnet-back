package org.socialnet.socialnet.user.infrastructure.persistence.jpa.adapter;

import lombok.AllArgsConstructor;
import org.socialnet.socialnet.user.core.model.User;
import org.socialnet.socialnet.user.core.port.input.OnboardingUseCase;
import org.socialnet.socialnet.user.core.port.output.UserRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.ProfileEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserSettingsEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.mapper.UserPersistenceMapper;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.ProfileJpaRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.UserJpaRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.UserSettingsJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper mapper;
    private final UserSettingsJpaRepository userSettingsJpaRepository;
    private final ProfileJpaRepository profileJpaRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String ONLINE_USERS_KEY = "user:online:";


    @Override
    public User findOrCreate(String userId, String login, String email) {
        var existingUser = userJpaRepository.findById(userId);
        if (existingUser.isPresent()) {
            return mapper.toDomain(existingUser.get());
        }

        try {
            return createUserInTransaction(userId, login, email);
        } catch (DataIntegrityViolationException e) {
            return mapper.toDomain(userJpaRepository.findById(userId).orElseThrow());
        }
    }

    @Override
    public User findById(String userId) {
        return mapper.toDomain(userJpaRepository.findById(userId).orElseThrow());
    }

    @Transactional
    public User createUserInTransaction(String userId, String login, String email) {
        UserEntity newUser = new UserEntity(userId, login, email);

        UserEntity savedUser = userJpaRepository.save(newUser);

        ProfileEntity newProfile = new ProfileEntity();

        newProfile.setId(savedUser.getId());

        profileJpaRepository.save(newProfile);

        UserSettingsEntity newSettings = new UserSettingsEntity();

        newSettings.setUserId(savedUser.getId());

        userSettingsJpaRepository.save(newSettings);

        return mapper.toDomain(savedUser);
    }

    @Override
    public void updateUserOnboardingStep(String userId, int onboardingStep) {
        var userEntityOptional = userJpaRepository.findById(userId);
        if (userEntityOptional.isPresent()) {
            var userEntity = userEntityOptional.get();
            userEntity.setOnboardingStep(onboardingStep);
            userJpaRepository.save(userEntity);
        }
    }

    @Override
    public int getUserOnboardingStep(String userId) {
        var userEntityOptional = userJpaRepository.findById(userId);
        return userEntityOptional.map(UserEntity::getOnboardingStep).orElse(0);
    }

    public boolean isUserOnline(String userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(ONLINE_USERS_KEY + userId));
    }

    public Map<String, Boolean> getUsersOnlineStatus(List<String> userIds) {
        Map<String, Boolean> statuses = new HashMap<>();
        for (String id : userIds) {
            statuses.put(id, isUserOnline(id));
        }
        return statuses;
    }

}
