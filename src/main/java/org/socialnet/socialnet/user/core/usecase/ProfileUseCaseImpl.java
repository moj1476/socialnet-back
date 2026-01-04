package org.socialnet.socialnet.user.core.usecase;

import org.socialnet.socialnet.user.core.model.CurrentProfile;
import org.socialnet.socialnet.user.core.model.Friend;
import org.socialnet.socialnet.user.core.model.MiniProfile;
import org.socialnet.socialnet.user.core.model.Profile;
import org.socialnet.socialnet.user.core.port.input.ProfileUseCase;
import org.socialnet.socialnet.user.core.port.output.ProfileRepository;

import java.util.List;
import java.util.Optional;

public class ProfileUseCaseImpl implements ProfileUseCase {
    private final ProfileRepository profileRepository;

    public ProfileUseCaseImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<CurrentProfile> getProfile(String userId) {
        var profile = profileRepository.findById(userId);
        return profile;
    }

    @Override
    public Optional<CurrentProfile> updateProfile(Profile profile) {
        var updatedProfile = profileRepository.updateProfile(profile);
        return updatedProfile;
    }

    @Override
    public List<PossibleFriend> findPossibleFriend(String userId) {
        return profileRepository.findPossibleFriends(userId);
    }

    @Override
    public List<Friend> getFriends(String userId, String profileId) {
        return profileRepository.getFriends(userId, profileId);
    }

    @Override
    public void addFriend(String userId, String friendId) {
        profileRepository.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        profileRepository.removeFriend(userId, friendId);
    }

    @Override
    public List<Friend> getWaitingFriends(String userId) {
        return profileRepository.getWaitingFriends(userId);
    }

    @Override
    public void addWaitingFriend(String userId, String friendId) {
        profileRepository.addWaitingFriend(userId, friendId);
    }

    @Override
    public void removeWaitingFriend(String userId, String friendId) {
        profileRepository.removeWaitingFriend(userId, friendId);
    }

    @Override
    public void rejectWaitingFriend(String userId, String friendId) {
        profileRepository.rejectWaitingFriend(userId, friendId);
    }

    @Override
    public void blockWaitingFriend(String userId, String friendId) {
        profileRepository.blockWaitingFriend(userId, friendId);
    }

    @Override
    public List<Friend> getSentFriendRequests(String userId) {
        return profileRepository.getSentFriendRequests(userId);
    }

    @Override
    public void removeSentFriendRequest(String userId, String friendId) {
        profileRepository.removeSentFriendRequest(userId, friendId);
    }

    @Override
    public List<Friend> findFriendByName(String userId, String name) {
        return List.of();
    }

    @Override
    public List<MiniProfile> searchProfilesByName(String nameQuery) {
        return profileRepository.searchProfilesByName(nameQuery);
    }


}
