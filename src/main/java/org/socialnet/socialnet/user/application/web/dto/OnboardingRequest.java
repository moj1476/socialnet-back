package org.socialnet.socialnet.user.application.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OnboardingRequest(
        @NotNull(message = "Шаг не может быть пустым")
        @Min(1)
        @Max(5)
        Integer step
) {}
