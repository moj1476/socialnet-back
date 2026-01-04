package org.socialnet.socialnet.user.application.web.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record ProfileRequest(
        @Size(min = 1, max = 100, message = "Имя должно содержать от 1 до 100 символов")
        String firstName,

        @Size(min = 1, max = 100, message = "Фамилия должна содержать от 1 до 100 символов")
        String lastName,

        @Size(max = 2000, message = "Биография не может превышать 2000 символов")
        String bio,

        @Size(max = 255, message = "Название города не может превышать 255 символов")
        String city,

        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate,

        @Size(max = 255, message = "Место учебы/работы не может превышать 255 символов")
        String workOrStudy,

        @Size(max = 20, message = "Можно выбрать не более 20 интересов")
        List<String> interests
) {
}
