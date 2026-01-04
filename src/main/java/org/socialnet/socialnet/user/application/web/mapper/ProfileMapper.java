package org.socialnet.socialnet.user.application.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.socialnet.socialnet.user.application.web.dto.MiniProfileResponse;
import org.socialnet.socialnet.user.application.web.dto.ProfileRequest;
import org.socialnet.socialnet.user.application.web.dto.ProfileResponse;
import org.socialnet.socialnet.user.core.model.CurrentProfile;
import org.socialnet.socialnet.user.core.model.MiniProfile;
import org.socialnet.socialnet.user.core.model.Profile;

import java.util.Optional;


@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileResponse toResponse(CurrentProfile profile);

    default Optional<ProfileResponse> toResponse(Optional<CurrentProfile> profile) {
        return profile.map(this::toResponse);
    }

    Profile toProfile(CurrentProfile profile);

    default Optional<Profile> toProfile(Optional<CurrentProfile> profile) {
        return profile.map(this::toProfile);
    }

    MiniProfileResponse toMiniProfileResponse(MiniProfile miniProfile);

    @Mapping(source = "userId", target = "id")
    @Mapping(target = ".", source = "req")
    Profile toModel(String userId, ProfileRequest req);
}
