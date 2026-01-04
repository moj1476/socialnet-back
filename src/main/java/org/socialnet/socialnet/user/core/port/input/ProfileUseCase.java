package org.socialnet.socialnet.user.core.port.input;

import org.socialnet.socialnet.user.core.model.CurrentProfile;
import org.socialnet.socialnet.user.core.model.Friend;
import org.socialnet.socialnet.user.core.model.MiniProfile;
import org.socialnet.socialnet.user.core.model.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileUseCase {
    Optional<CurrentProfile> getProfile(String userId);
    Optional<CurrentProfile> updateProfile(Profile profile);
    List<PossibleFriend> findPossibleFriend(String userId);
    List<Friend> getFriends(String userId, String profileId);
    void addFriend(String userId, String friendId);
    void removeFriend(String userId, String friendId);
    List<Friend> getWaitingFriends(String userId);
    void addWaitingFriend(String userId, String friendId);
    void removeWaitingFriend(String userId, String friendId);
    void rejectWaitingFriend(String userId, String friendId);
    void blockWaitingFriend(String userId, String friendId);
    List<Friend> getSentFriendRequests(String userId);
    void removeSentFriendRequest(String userId, String friendId);
    List<Friend> findFriendByName(String userId, String name);
    List<MiniProfile> searchProfilesByName(String nameQuery);


    record PossibleFriend(
            Optional<Profile> profile,
            int commonInterestsCount
    ){}
}
