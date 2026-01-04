package org.socialnet.socialnet.user.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.socialnet.socialnet.user.core.model.Friend;
import org.socialnet.socialnet.user.core.port.output.UserRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.adapter.UserRepositoryImpl;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.FriendshipEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.ProfileEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.FriendshipJpaRepository;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.ProfileJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class FriendshipPersistenceMapper {

    @Autowired
    protected ProfileJpaRepository profileJpaRepository;

    @Autowired
    protected UserRepositoryImpl userRepository; //TODO: переделать

    @Autowired
    protected FriendshipJpaRepository friendshipJpaRepository;

    public List<Friend> toFriendsList(List<FriendshipEntity> friendships, String currentUserId, String userId) {
        if (friendships == null || friendships.isEmpty()) {
            return List.of();
        }

        Set<String> friendIds = friendships.stream()
                .map(f -> f.getRequesterId().equals(currentUserId) ? f.getAddresseeId() : f.getRequesterId())
                .collect(Collectors.toSet());
        
        Map<String, ProfileEntity> profilesMap = profileJpaRepository.findAllById(friendIds).stream()
                .collect(Collectors.toMap(ProfileEntity::getId, profile -> profile));

        return friendships.stream().map(friendship -> {
            String friendId = friendship.getRequesterId().equals(currentUserId) 
                    ? friendship.getAddresseeId() 
                    : friendship.getRequesterId();

            if(friendId.equals(userId)) {
                return null;
            }
            ProfileEntity friendProfile = profilesMap.get(friendId);
            if (friendProfile == null) return null;
            boolean isOnline = userRepository.isUserOnline(friendId);
            boolean areFriends = friendshipJpaRepository.findFriendshipBetween(userId, friendId).isPresent();

            return new Friend(
                    friendProfile.getId(),
                    friendProfile.getFirstName(),
                    friendProfile.getLastName(),
                    friendProfile.getAvatarUrl(),
                    friendship.getStatus().name(),
                    isOnline,
                    areFriends
            );
        }).filter(java.util.Objects::nonNull).toList();
    }
}