package org.socialnet.socialnet.post.application.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.message.application.web.dto.CallSignalResponse;
import org.socialnet.socialnet.post.application.web.dto.AddCommentRequest;
import org.socialnet.socialnet.post.application.web.dto.CommentResponse;
import org.socialnet.socialnet.post.application.web.dto.NotificationResponse;
import org.socialnet.socialnet.post.application.web.mapper.CommentApiMapper;
import org.socialnet.socialnet.post.core.model.Comment;
import org.socialnet.socialnet.post.core.port.input.CommentUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comments", description = "API для работы с комментариями")
public class CommentController {

    private final CommentUseCase commentUseCase;
    private final CommentApiMapper commentApiMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "Добавить комментарий к посту")
    public ResponseEntity<CommentResponse> addComment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId,
            @RequestBody @Valid AddCommentRequest request
    ) {
        String userId = jwt.getSubject();
        
        Comment comment = commentUseCase.addComment(
                postId, 
                userId, 
                request.content(), 
                request.parentId()
        );

        messagingTemplate.convertAndSendToUser(
                comment.postAuthorId(),
                "/queue/notifications",
                new NotificationResponse(
                        "NEW_COMMENT",
                        "Новый комментарий к вашему посту",
                        "У пользователя " + comment.author().firstName() + " новый комментарий к вашему посту.",
                        null
                ));

        return ResponseEntity.ok(commentApiMapper.toResponse(comment));
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "Получить список комментариев к посту")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long postId
    ) {
        List<Comment> comments = commentUseCase.getComments(postId);
        return ResponseEntity.ok(commentApiMapper.toResponseList(comments));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Удалить комментарий (только автор)")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long commentId
    ) {
        String userId = jwt.getSubject();
        commentUseCase.deleteComment(commentId, userId);
        return ResponseEntity.ok().build();
    }
}