package org.socialnet.socialnet.auth.application.web.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Логин не должен быть пустым")
        String login,

        @NotBlank(message = "Пароль не должен быть пустым")
        String password) {}
