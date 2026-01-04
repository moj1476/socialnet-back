package org.socialnet.socialnet.auth.application.web.mapper;

import org.mapstruct.Mapper;
import org.socialnet.socialnet.auth.application.web.dto.TokenResponse;
import org.socialnet.socialnet.auth.core.model.JwtTokens;

@Mapper(componentModel = "spring")
public interface AuthApiMapper {

    TokenResponse toTokenResponse(JwtTokens token);

}
