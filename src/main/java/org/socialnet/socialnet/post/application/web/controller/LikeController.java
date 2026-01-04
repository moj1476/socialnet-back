package org.socialnet.socialnet.post.application.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.post.core.port.input.LikeUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Likes", description = "API для лайков")
public class LikeController {

    private final LikeUseCase likeUseCase;

    @PostMapping("/{postId}/like")
    @Operation(summary = "Поставить лайк посту")
    public ResponseEntity<Void> likePost(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId
    ) {
        String userId = jwt.getSubject();
        likeUseCase.likePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    @Operation(summary = "Убрать лайк с поста")
    public ResponseEntity<Void> unlikePost(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId
    ) {
        String userId = jwt.getSubject();
        likeUseCase.unlikePost(postId, userId);
        return ResponseEntity.ok().build();
    }
}