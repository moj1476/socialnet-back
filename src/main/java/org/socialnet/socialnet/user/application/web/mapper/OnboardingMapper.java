package org.socialnet.socialnet.user.application.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.socialnet.socialnet.user.application.web.dto.OnboardingParams;
import org.socialnet.socialnet.user.application.web.dto.OnboardingResponse;
import org.socialnet.socialnet.user.application.web.dto.ProfileResponse;
import org.socialnet.socialnet.user.core.port.input.OnboardingUseCase.*;

import java.util.Optional;


@Mapper(componentModel = "spring")
public interface OnboardingMapper {

    @Mapping(source = "step", target = "onboardingStep")
    UpdateOnboardingStepCommand toCommand(String userId, int step);

    OnboardingParams toResponseParams(GetOnboardingStepResponse res);

    @Mapping(source = "onboarding", target = "onboarding")
    OnboardingResponse toResponse(OnboardingParams onboarding, Optional<ProfileResponse> profile);

}
