package org.socialnet.socialnet.user.application.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record ChangeAvatarResponse(
        @JsonProperty("avatar")
        @NotNull
        String newAvatarUrl
) {};