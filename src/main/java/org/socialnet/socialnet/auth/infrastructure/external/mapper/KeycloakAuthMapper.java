package org.socialnet.socialnet.auth.infrastructure.external.mapper;


import org.mapstruct.Mapper;
import org.socialnet.socialnet.auth.core.model.AuthenticatedUser;
import org.socialnet.socialnet.auth.core.model.JwtTokens;
import org.socialnet.socialnet.auth.infrastructure.external.dto.KeycloakAuthResponse;

@Mapper(componentModel = "spring")
public interface KeycloakAuthMapper {
    JwtTokens toDomain(KeycloakAuthResponse entity);
}
