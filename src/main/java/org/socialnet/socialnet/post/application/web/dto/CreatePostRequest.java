package org.socialnet.socialnet.post.application.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public record CreatePostRequest(
        @Schema(description = "Текст поста")
        String content,

        @Schema(description = "Тип вложения: IMAGE, VIDEO, POLL, TEXT", defaultValue = "TEXT")
        String attachmentType,

        @Schema(description = "Файлы (если тип IMAGE или VIDEO)")
        List<MultipartFile> files,

        @Schema(description = "Вопрос для опроса (если тип POLL)")
        String pollQuestion,

        @Schema(description = "Варианты ответов (если тип POLL)")
        @Size(min = 2, message = "Опрос должен содержать минимум 2 варианта ответа")
        List<String> pollOptions
) {}