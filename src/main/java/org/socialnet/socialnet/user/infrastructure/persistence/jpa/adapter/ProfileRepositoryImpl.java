package org.socialnet.socialnet.user.infrastructure.persistence.jpa.adapter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.socialnet.socialnet.post.application.web.dto.NotificationResponse;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.PostJpaRepository;
import org.socialnet.socialnet.user.core.model.CurrentProfile;
import org.socialnet.socialnet.user.core.model.Friend;
import org.socialnet.socialnet.user.core.model.MiniProfile;
import org.socialnet.socialnet.user.core.model.Profile;
import org.socialnet.socialnet.user.core.port.input.ProfileUseCase;
import org.socialnet.socialnet.user.core.port.output.ProfileRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.FriendshipEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.FriendshipStatusDb;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.ProfileEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.mapper.FriendshipPersistenceMapper;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.mapper.ProfilePersistenceMapper;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.FriendshipJpaRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.ProfileJpaRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfilePersistenceMapper mapper;
    private final UserJpaRepository userJpaRepository;
    private final ProfileJpaRepository profileJpaRepository;
    private final FriendshipJpaRepository friendshipJpaRepository;
    private final FriendshipPersistenceMapper friendshipMapper;
    private final PostJpaRepository postJpaRepository;
    private final SimpMessagingTemplate messagingTemplate;



    @Override
    public void updateAvatarUrl(String userId, String avatarUrl) {
        var profileEntityOptional = profileJpaRepository.findById(userId);
        if (profileEntityOptional.isPresent()) {
            var profileEntity = profileEntityOptional.get();
            profileEntity.setAvatarUrl(avatarUrl);
            profileJpaRepository.save(profileEntity);
        } else {
            var profileEntity = new ProfileEntity();
            profileEntity.setId(userId);
            profileEntity.setAvatarUrl(avatarUrl);
            profileJpaRepository.save(profileEntity);
        }
    }

    @Override
    public Optional<CurrentProfile> findById(String userId) {
        Optional<ProfileEntity> profileOpt = profileJpaRepository.findById(userId);
        Optional<UserEntity> userOpt = userJpaRepository.findById(userId);

        if (profileOpt.isEmpty() || userOpt.isEmpty()) {
            return Optional.empty();
        }

        ProfileEntity profile = profileOpt.get();

        CurrentProfile currentProfile = mapper.toCurrentProfile(
                profile
        );

        return Optional.of(currentProfile);
    }

    @Override
    public Optional<CurrentProfile> updateProfile(Profile profile) {
        var profileEntityOptional = profileJpaRepository.findById(profile.getId());
        if (profileEntityOptional.isPresent()) {
            mapper.updateEntityFromDomain(profile, profileEntityOptional.get());
            var updatedProfileEntity = profileJpaRepository.save(profileEntityOptional.get());
            CurrentProfile currentProfile = mapper.toCurrentProfile(
                    updatedProfileEntity
            );

            return Optional.of(currentProfile);
        }
        return Optional.empty();
    }

    @Override
    public List<Friend> getFriends(String userId, String profileId) {
        List<FriendshipEntity> friendEntities = friendshipJpaRepository.findFriendsByUserId(profileId);
        return friendshipMapper.toFriendsList(friendEntities, profileId, userId);
    }

    @Override
    public void addFriend(String userId, String friendId) {
        Optional<FriendshipEntity> existingFriendship = friendshipJpaRepository.findFriendshipBetween(userId, friendId);

        if (existingFriendship.isPresent()) {
            FriendshipEntity friendship = existingFriendship.get();
            if (friendship.getStatus() == FriendshipStatusDb.DECLINED) {
                friendship.setStatus(FriendshipStatusDb.PENDING);
                friendship.setRequesterId(userId);
                friendship.setAddresseeId(friendId);
                friendshipJpaRepository.save(friendship);
            }
            return;
        }

        FriendshipEntity newFriendship = new FriendshipEntity();
        newFriendship.setRequesterId(userId);
        newFriendship.setAddresseeId(friendId);
        newFriendship.setStatus(FriendshipStatusDb.PENDING);
        friendshipJpaRepository.save(newFriendship);


        var profile = findById(userId).get();
        messagingTemplate.convertAndSendToUser(
                friendId,
                "/queue/notifications",
                new NotificationResponse(
                        "NEW_FRIEND_REQUEST",
                        "Новый друг",
                        "Пользователь " + profile.firstName() + " хочет добавить вас в друзья.",
                        null
                ));
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        friendshipJpaRepository.findFriendshipBetween(userId, friendId)
                .ifPresent(friendshipJpaRepository::delete);
    }

    @Override
    public List<Friend> getWaitingFriends(String userId) {
        List<FriendshipEntity> requestEntities = friendshipJpaRepository.findIncomingFriendRequests(userId);
        return friendshipMapper.toFriendsList(requestEntities, userId, userId);
    }

    @Override
    public void addWaitingFriend(String userId, String friendId) {
        Optional<FriendshipEntity> request = friendshipJpaRepository
                .findByRequesterIdAndAddresseeIdAndStatus(friendId, userId, FriendshipStatusDb.PENDING);

        request.ifPresent(friendship -> {
            friendship.setStatus(FriendshipStatusDb.ACCEPTED);
            friendship.setUpdatedAt(Instant.now());
            friendshipJpaRepository.save(friendship);

            var profile = findById(userId).get();
            messagingTemplate.convertAndSendToUser(
                    friendId,
                    "/queue/notifications",
                    new NotificationResponse(
                            "NEW_FRIEND",
                            "Приглашение принято",
                            "Пользователь " + profile.firstName() + " добавил вас в друзья.",
                            null
                    ));
        });
    }

    @Override
    public void removeWaitingFriend(String userId, String friendId) {
        Optional<FriendshipEntity> request = friendshipJpaRepository
                .findByRequesterIdAndAddresseeIdAndStatus(friendId, userId, FriendshipStatusDb.PENDING);

        request.ifPresent(friendshipJpaRepository::delete);
    }

    @Override
    public void rejectWaitingFriend(String userId, String friendId) {
        Optional<FriendshipEntity> pendingRequest = friendshipJpaRepository.findByRequesterIdAndAddresseeIdAndStatus(
                friendId,
                userId,
                FriendshipStatusDb.PENDING
        );

        pendingRequest.ifPresent(friendship -> {
            friendship.setStatus(FriendshipStatusDb.DECLINED);
            friendship.setUpdatedAt(Instant.now());
            friendshipJpaRepository.save(friendship);
        });
    }

    @Override
    public void blockWaitingFriend(String userId, String friendId) {
        Optional<FriendshipEntity> existingRelationship = friendshipJpaRepository.findFriendshipBetween(userId, friendId);

        if (existingRelationship.isPresent()) {
            FriendshipEntity friendship = existingRelationship.get();
            friendship.setStatus(FriendshipStatusDb.BLOCKED);
            friendship.setRequesterId(userId);
            friendship.setAddresseeId(friendId);
            friendship.setUpdatedAt(Instant.now());
            friendshipJpaRepository.save(friendship);
        } else {
            FriendshipEntity blockRelationship = new FriendshipEntity();
            blockRelationship.setRequesterId(userId);
            blockRelationship.setAddresseeId(friendId);
            blockRelationship.setStatus(FriendshipStatusDb.BLOCKED);
            friendshipJpaRepository.save(blockRelationship);
        }
    }

    @Override
    public List<Friend> getSentFriendRequests(String userId) {
        List<FriendshipEntity> sentRequests = friendshipJpaRepository.findByRequesterIdAndStatus(userId, FriendshipStatusDb.PENDING);

        return friendshipMapper.toFriendsList(sentRequests, userId, userId);
    }

    @Override
    public void removeSentFriendRequest(String userId, String friendId) {
        Optional<FriendshipEntity> sentRequest = friendshipJpaRepository.findByRequesterIdAndAddresseeIdAndStatus(
                userId,
                friendId,
                FriendshipStatusDb.PENDING
        );

        sentRequest.ifPresent(friendshipJpaRepository::delete);
    }

    @Override
    public List<Friend> findFriendByName(String userId, String name) {
        return List.of();
    }

    @Override
    public List<MiniProfile> searchProfilesByName(String nameQuery) {
        List<ProfileEntity> foundEntities = profileJpaRepository.searchProfilesByNameQuery(nameQuery);

        if (foundEntities.isEmpty()) {
            return List.of();
        }

        Set<String> userIds = foundEntities.stream()
                .map(ProfileEntity::getId)
                .collect(Collectors.toSet());


        return foundEntities.stream()
                .map(entity -> new MiniProfile(
                        entity.getId(),
                        entity.getFirstName(),
                        entity.getLastName(),
                        entity.getAvatarUrl(),
                        true
                ))
                .toList(); //TODO: прикрутить маппер
    }

    @Override
    public List<ProfileUseCase.PossibleFriend> findPossibleFriends(String userId) {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProfileJpaRepository.PossibleFriendProjection> projections = profileJpaRepository.findPotentialFriendsByInterests(userId, pageable);

        return projections.stream()
                .map(mapper::projectionToPossibleFriend)
                .toList();
    }

    @Override
    public Map<String, String> findNamesByIds(Set<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        List<ProfileEntity> profiles = profileJpaRepository.findAllById(userIds);

        return profiles.stream()
                .collect(Collectors.toMap(
                        ProfileEntity::getId,
                        profile -> (profile.getFirstName().trim()
                )));
    }
}
