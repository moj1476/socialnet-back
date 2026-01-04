package org.socialnet.socialnet.post.application.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.post.application.web.dto.PostsFeedResponse;
import org.socialnet.socialnet.post.application.web.mapper.PostApiMapper;
import org.socialnet.socialnet.post.core.model.PostSummary;
import org.socialnet.socialnet.post.core.port.input.RepostUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Reposts", description = "API для репостов (Поделиться)")
public class RepostController {

    private final RepostUseCase repostUseCase;
    private final PostApiMapper postApiMapper;

    @PostMapping("/{postId}/repost")
    @Operation(summary = "Сделать репост (поделиться)", description = "Добавляет пост в коллекцию пользователя")
    public ResponseEntity<Void> repostPost(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "false") boolean isPrivate
    ) {
        String userId = jwt.getSubject();

        repostUseCase.repostPost(userId, postId, isPrivate);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/repost")
    @Operation(summary = "Удалить репост")
    public ResponseEntity<Void> removeRepost(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "false") boolean isPrivate
    ) {
        String userId = jwt.getSubject();
        repostUseCase.removeRepost(userId, postId, isPrivate);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reposts")
    @Operation(summary = "Получить ленту постов пользователя")
    public ResponseEntity<List<PostsFeedResponse>> getPosts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam() String profileId,
            @RequestParam(defaultValue = "false") boolean isPrivate
    ) {
        String userId = jwt.getSubject();
        List<PostSummary> result = null;

        if(isPrivate) {
            result = repostUseCase.getReposts(userId, true);
        } else {
            result = repostUseCase.getReposts(profileId, false);
        }

        var response = postApiMapper.toResponseList(result);

        return ResponseEntity.ok(response);
    }
}