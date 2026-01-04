package org.socialnet.socialnet.user.core.port.output;

import org.socialnet.socialnet.user.core.model.CurrentProfile;
import org.socialnet.socialnet.user.core.model.Friend;
import org.socialnet.socialnet.user.core.model.MiniProfile;
import org.socialnet.socialnet.user.core.model.Profile;
import org.socialnet.socialnet.user.core.port.input.ProfileUseCase;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ProfileRepository {
    void updateAvatarUrl(String userId, String avatarUrl);
    Optional<CurrentProfile> findById(String userId);
    Optional<CurrentProfile> updateProfile(Profile profile);
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

    List<ProfileUseCase.PossibleFriend> findPossibleFriends(String userId);
    Map<String, String> findNamesByIds(Set<String> userIds);
}
