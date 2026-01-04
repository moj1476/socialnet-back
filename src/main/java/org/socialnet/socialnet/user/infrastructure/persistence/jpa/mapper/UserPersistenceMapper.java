package org.socialnet.socialnet.user.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.socialnet.socialnet.user.core.model.Profile;
import org.socialnet.socialnet.user.core.model.User;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.ProfileEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    UserEntity toEntity(User user);


    @ObjectFactory
    default User of(UserEntity entity) {
        return User.of(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getOnboardingStep(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }


    User toDomain(UserEntity entity);

}
