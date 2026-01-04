package org.socialnet.socialnet.user.infrastructure.persistence.jpa.mapper;

import org.mapstruct.*;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.LikeJpaRepository;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.PostJpaRepository;
import org.socialnet.socialnet.user.core.model.CurrentProfile;
import org.socialnet.socialnet.user.core.model.Profile;
import org.socialnet.socialnet.user.core.port.input.ProfileUseCase;
import org.socialnet.socialnet.user.core.port.output.ProfileRepository;
import org.socialnet.socialnet.user.core.port.output.UserRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.adapter.UserRepositoryImpl;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.ProfileEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.FriendshipJpaRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.ProfileJpaRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProfilePersistenceMapper {

    @Autowired
    protected UserJpaRepository userJpaRepository;
    @Autowired
    protected PostJpaRepository postJpaRepository;
     @Autowired
     protected FriendshipJpaRepository friendshipJpaRepository;
     @Autowired
     protected LikeJpaRepository likeJpaRepository;
     @Autowired
     protected UserRepositoryImpl userRepository;


    public CurrentProfile toCurrentProfile(ProfileEntity profileEntity) {
        if (profileEntity == null) {
            return null;
        }

        String userId = profileEntity.getId();

        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElse(new UserEntity());

        Integer postsCount = postJpaRepository.countByAuthorId(userId);
        var friendsCount =  friendshipJpaRepository.countFriendsByUserId(userId);
        var likesCount = likeJpaRepository.countLikesReceivedByUser(userId);
        boolean isOnline = userRepository.isUserOnline(userId);

        return new CurrentProfile(
                profileEntity.getId(),
                profileEntity.getFirstName(),
                profileEntity.getLastName(),
                profileEntity.getBio(),
                profileEntity.getAvatarUrl(),
                profileEntity.getCity(),
                profileEntity.getBirthDate(),
                profileEntity.getWorkOrStudy(),
                profileEntity.getInterests(),
                likesCount,
                friendsCount,
                postsCount,
                isOnline,
                userEntity.getUsername(),
                profileEntity.getCreatedAt(),
                profileEntity.getUpdatedAt()
        );
    }


    @Mapping(target = "id", ignore = true)
    public abstract ProfileEntity toEntity(Profile profile);


    @ObjectFactory
    public Profile of(ProfileEntity entity) {
        return Profile.of(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBio(),
                entity.getAvatarUrl(),
                entity.getInterests(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }


    public abstract Profile toDomain(ProfileEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntityFromDomain(Profile profile, @MappingTarget ProfileEntity profileEntity);
    @Mapping(source = "interests", target = "interests", qualifiedByName = "stringToInterestList")
    public abstract Profile projectionToProfile(ProfileJpaRepository.PossibleFriendProjection projection);

    @Mapping(target = "profile", expression = "java(Optional.of(projectionToProfile(projection)))")
    @Mapping(source = "commonInterestsCount", target = "commonInterestsCount")
    public abstract ProfileUseCase.PossibleFriend projectionToPossibleFriend(ProfileJpaRepository.PossibleFriendProjection projection);

    @Named("stringToInterestList")
    public List<String> stringToInterestList(String interests) {
        if (interests == null || interests.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(interests.split(","));
    }


}
