package org.socialnet.socialnet.message.application.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageDto(
        @NotBlank
        @Size(max = 4096)
        String content
) {
}