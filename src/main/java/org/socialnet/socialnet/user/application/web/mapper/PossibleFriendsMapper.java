package org.socialnet.socialnet.user.application.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.socialnet.socialnet.user.application.web.dto.ChangeAvatarResponse;
import org.socialnet.socialnet.user.application.web.dto.PossibleFriendsResponse;
import org.socialnet.socialnet.user.core.model.Profile;
import org.socialnet.socialnet.user.core.port.input.ChangeAvatarUseCase.*;
import org.socialnet.socialnet.user.core.port.input.ProfileUseCase;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PossibleFriendsMapper {


    default PossibleFriendsResponse toResponse(ProfileUseCase.PossibleFriend possibleFriend) {
        if (possibleFriend.profile().isEmpty()) {
            return null;
        }

        Profile profile = possibleFriend.profile().get();
        int commonInterestsCount = possibleFriend.commonInterestsCount();

        return toResponse(profile, commonInterestsCount);
    }

    @Mapping(source = "profile.id", target = "id")
    @Mapping(source = "profile.firstName", target = "firstName")
    @Mapping(source = "profile.lastName", target = "lastName")
    @Mapping(source = "profile.avatarUrl", target = "avatarUrl")
    @Mapping(source = "profile.city", target = "city")
    @Mapping(source = "commonInterestsCount", target = "commonInterestsCount")
    @Mapping(target = "isOnline", ignore = true)
    PossibleFriendsResponse toResponse(Profile profile, int commonInterestsCount);

}
