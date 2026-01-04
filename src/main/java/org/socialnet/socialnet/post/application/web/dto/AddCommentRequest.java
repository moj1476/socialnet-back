package org.socialnet.socialnet.post.application.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddCommentRequest(
        @Schema(description = "Текст комментария")
        @NotBlank(message = "Комментарий не может быть пустым")
        @Size(max = 1000, message = "Комментарий слишком длинный")
        String content,

        @Schema(description = "ID родительского комментария (если это ответ)", nullable = true)
        Long parentId
) {}