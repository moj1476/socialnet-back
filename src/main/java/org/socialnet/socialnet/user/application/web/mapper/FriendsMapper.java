package org.socialnet.socialnet.user.application.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.socialnet.socialnet.user.application.web.dto.FriendResponse;
import org.socialnet.socialnet.user.application.web.dto.ProfileRequest;
import org.socialnet.socialnet.user.application.web.dto.ProfileResponse;
import org.socialnet.socialnet.user.core.model.CurrentProfile;
import org.socialnet.socialnet.user.core.model.Friend;
import org.socialnet.socialnet.user.core.model.Profile;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface FriendsMapper {

    FriendResponse toResponse(Friend friend);

    List<FriendResponse> toResponse(List<Friend> friends);
}
