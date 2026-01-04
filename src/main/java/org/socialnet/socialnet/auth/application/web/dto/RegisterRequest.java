package org.socialnet.socialnet.auth.application.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
        @NotBlank(message = "Логин не должен быть пустым")
        @Size(min = 3, max = 20, message = "Логин должен быть от 3 до 20 символов")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Логин может содержать только буквы, цифры и символы подчеркивания")
        String login,

        @NotBlank(message = "Email не должен быть пустым")
        @Size(max = 50, message = "Email не должен превышать 50 символов")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Некорректный формат email")
        String email,

        @NotBlank(message = "Пароль не должен быть пустым")
        @Size(min = 6, max = 100, message = "Пароль должен быть от 6 до 100 символов")
        String password
) { }
