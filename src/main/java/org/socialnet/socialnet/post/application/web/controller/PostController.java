package org.socialnet.socialnet.post.application.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.socialnet.socialnet.post.application.web.dto.CreatePostRequest;
import org.socialnet.socialnet.post.application.web.dto.PostsFeedResponse;
import org.socialnet.socialnet.post.application.web.dto.VoteRequest;
import org.socialnet.socialnet.post.application.web.mapper.PostApiMapper;
import org.socialnet.socialnet.post.core.model.Post;
import org.socialnet.socialnet.post.core.model.TrendingTopic;
import org.socialnet.socialnet.post.core.port.input.PostUseCase;
import org.socialnet.socialnet.shared.core.port.output.FileStoragePort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostUseCase postUseCase;
    private final PostApiMapper postApiMapper;
    private final FileStoragePort fileStoragePort;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать новый пост", description = "Поддерживает текст, множество фото, видео и опросы")
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal Jwt jwt,
            @ModelAttribute @Valid CreatePostRequest request
    ) {
        String authorId = jwt.getSubject();
        List<String> fileUrls = new ArrayList<>();

        if (request.files() != null && !request.files().isEmpty()) {
            for (MultipartFile file : request.files()) {
                if (file.isEmpty()) continue;

                try {
                    String url = fileStoragePort.save(
                            file.getInputStream(),
                            file.getOriginalFilename(),
                            "posts"
                    );
                    fileUrls.add(url);
                } catch (IOException e) {
                    log.error("Failed to upload file for user {}", authorId, e);
                    throw new RuntimeException("Ошибка загрузки файла " + file.getOriginalFilename(), e);
                }
            }
        }

        Post post = postApiMapper.toDomain(request, authorId, fileUrls);
        postUseCase.createPost(post);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{postId}")
    @Operation(summary = "Удалить пост по ID")
    public ResponseEntity<Void> removePost(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId
    ) {
        String userId = jwt.getSubject();
        postUseCase.removePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Получить ленту постов пользователя")
    public ResponseEntity<List<PostsFeedResponse>> getPosts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String authorId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        String userId = jwt.getSubject();

        var result = postUseCase.getPosts(userId, authorId, type, page, limit);

        var response = postApiMapper.toResponseList(result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/vote")
    public ResponseEntity<Void> vote(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid VoteRequest request
    ) {
        String userId = jwt.getSubject();
        postUseCase.voteInPoll(userId, request.postId(), request.variant());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trending")
    @Operation(summary = "Получить топ-4 актуальных тем за 24 часа")
    public ResponseEntity<List<TrendingTopic>> getTrending() {
        return ResponseEntity.ok(postUseCase.getTrendingTopics());
    }
}