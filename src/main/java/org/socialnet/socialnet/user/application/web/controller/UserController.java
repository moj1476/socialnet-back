package org.socialnet.socialnet.user.application.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.socialnet.socialnet.user.application.web.dto.*;
import org.socialnet.socialnet.user.application.web.mapper.*;
import org.socialnet.socialnet.user.core.model.Friend;
import org.socialnet.socialnet.user.core.model.SettingsEntity;
import org.socialnet.socialnet.user.core.model.Theme;
import org.socialnet.socialnet.user.core.model.VisibilityType;
import org.socialnet.socialnet.user.core.port.input.*;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserSettingsEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final ChangeAvatarUseCase updateAvatarUseCase;
    private final ChangeAvatarMapper changeAvatarMapper;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final OnboardingUseCase onboardingUseCase;
    private final OnboardingMapper onboardingMapper;
    private final ProfileUseCase profileUseCase;
    private final UserSettingsUseCase userSettingsUseCase;
    private final ProfileMapper profileMapper;
    private final PossibleFriendsMapper possibleFriendsMapper;
    private final FriendsMapper friendsMapper;


    //TODO: move to separate file
    private static class FileUploadSchema {
        @Schema(type = "string", format = "binary")
        public MultipartFile file;
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Обновить аватар пользователя",
            description = "Загружает новый файл аватара. Запрос должен быть multipart/form-data.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Файл аватара, который нужно загрузить.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = FileUploadSchema.class)
                    )
            )
    )
    public ResponseEntity<ChangeAvatarResponse> updateAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        String userId = jwt.getSubject();
        var fileToUpload = changeAvatarMapper.toFile(file);

        var command = changeAvatarMapper.toCommand(userId, fileToUpload);

        var data = updateAvatarUseCase.handle(command);
        var response = changeAvatarMapper.toResponse(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<DecodedTokenResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        var user = getCurrentUserUseCase.handle(userId);
        String email = user.getEmail();
        String login = user.getUsername();
        var onboardingStep = onboardingMapper.toResponseParams(
                onboardingUseCase.getUserOnboardingStep(jwt.getSubject())
        );
        var profile = profileMapper.toResponse(
                profileUseCase.getProfile(userId)
        );
        var userInfo = new DecodedTokenResponse(userId, email, login, onboardingStep, profile);

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/onboarding")
    public ResponseEntity<OnboardingResponse> getOnboarding(@AuthenticationPrincipal Jwt jwt) {
        var params = onboardingMapper.toResponseParams(
                onboardingUseCase.getUserOnboardingStep(jwt.getSubject())
        );
        var profileOptional = profileUseCase.getProfile(jwt.getSubject());
        var profile = profileMapper.toResponse(
                profileOptional
        );
        var response = onboardingMapper.toResponse(params, profile);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/onboarding")
    public ResponseEntity<?> setOnboarding(
            @Valid @RequestBody OnboardingRequest onboardingRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var step = onboardingRequest.step();
        var command = onboardingMapper.toCommand(jwt.getSubject(), step);
        onboardingUseCase.updateUserOnboardingStep(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<Optional<ProfileResponse>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        var profileOptional = profileUseCase.getProfile(jwt.getSubject());
        var response = profileMapper.toResponse(
                profileOptional
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<Optional<ProfileResponse>> getProfileById(
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "Идентификатор пользователя", required = true)
            @PathVariable String userId
    ) {
        var profileOptional = profileUseCase.getProfile(userId);
        var response = profileMapper.toResponse(
                profileOptional
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Optional<ProfileResponse>> updateProfile(
            @Valid @RequestBody ProfileRequest profileRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var profile = profileMapper.toModel(jwt.getSubject(), profileRequest);
        var updatedProfileOptional = profileUseCase.updateProfile(profile);
        var response = profileMapper.toResponse(
                updatedProfileOptional
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/possible-friends")
    public ResponseEntity<List<PossibleFriendsResponse>> getPossibleFriends(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @AuthenticationPrincipal Jwt jwt
    ) {
        var possibleFriends = profileUseCase.findPossibleFriend(jwt.getSubject());
        List<PossibleFriendsResponse> response = possibleFriends.stream()
                .flatMap(possibleFriend ->
                        possibleFriend.profile().stream()
                                .map(profile -> {
                                    return possibleFriendsMapper.toResponse(possibleFriend);
                                })
                )
                .toList();

        return ResponseEntity.ok(response);

    }

    @GetMapping("/friends")
    public ResponseEntity<List<FriendResponse>> getFriends(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String profileId
    ) {
        var user = profileId != null ? profileId : jwt.getSubject();
        List<Friend> friends = profileUseCase.getFriends(jwt.getSubject(), user);
        return ResponseEntity.ok(
                friendsMapper.toResponse(friends)
        );
    }

    @PostMapping("/friends/add-friend")
    public ResponseEntity<?> addFriend(
            @Valid @RequestBody AddFriendRequest addFriendRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        profileUseCase.addFriend(jwt.getSubject(), addFriendRequest.friendId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @RequestMapping("/friends/remove-friend/{friendId}")
    public ResponseEntity<?> removeFriend(
            @Parameter(description = "Идентификатор друга", required = true)
            @PathVariable String friendId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        profileUseCase.removeFriend(jwt.getSubject(), friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/friends/waiting")
    public ResponseEntity<List<FriendResponse>> getWaitingFriends(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<Friend> friends = profileUseCase.getWaitingFriends(jwt.getSubject());
        return ResponseEntity.ok(
                friendsMapper.toResponse(friends)
        );
    }

    @PostMapping("/friends/waiting/add")
    public ResponseEntity<?> addWaitingFriend(
            @Valid @RequestBody AddFriendRequest addFriendRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        profileUseCase.addWaitingFriend(jwt.getSubject(), addFriendRequest.friendId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @RequestMapping("/friends/waiting/reject")
    public ResponseEntity<?> rejectWaitingFriend(
            @Valid @RequestBody AddFriendRequest addFriendRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        profileUseCase.rejectWaitingFriend(jwt.getSubject(), addFriendRequest.friendId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @RequestMapping("/friends/waiting/block")
    public ResponseEntity<?> blockWaitingFriend(
            @Valid @RequestBody AddFriendRequest addFriendRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        profileUseCase.blockWaitingFriend(jwt.getSubject(), addFriendRequest.friendId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @RequestMapping("/friends/sent-requests")
    public ResponseEntity<List<FriendResponse>> getSentFriendRequests(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<Friend> friends = profileUseCase.getSentFriendRequests(jwt.getSubject());
        return ResponseEntity.ok(
                friendsMapper.toResponse(friends)
        );
    }

    @DeleteMapping
    @RequestMapping("/friends/sent-requests/remove/{friendId}")
    public ResponseEntity<?> removeSentFriendRequest(
            @Parameter(description = "Идентификатор друга", required = true)
            @PathVariable String friendId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        profileUseCase.removeSentFriendRequest(jwt.getSubject(), friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profiles/search")
    public ResponseEntity<List<MiniProfileResponse>> searchProfilesByName(
            @Parameter(description = "Запрос для поиска по имени", required = true)
            @RequestParam String nameQuery
    ) {
        var miniProfiles = profileUseCase.searchProfilesByName(nameQuery);
        var response = miniProfiles.stream()
                .map(profileMapper::toMiniProfileResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить настройки текущего пользователя")
    @GetMapping("/settings")
    public ResponseEntity<Optional<SettingsEntity>> getMySettings(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(userSettingsUseCase.findByUserId(userId));
    }

    @Operation(summary = "Обновить настройки (частично)")
    @PatchMapping("/settings")
    public ResponseEntity<SettingsEntity> updateSettings(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateSettingsRequest request
    ) {
        String userId = jwt.getSubject();

        SettingsEntity currentSettings = userSettingsUseCase.findByUserId(userId)
                .orElse(new SettingsEntity(
                        userId,
                        true, true, true, true,
                        VisibilityType.EVERYONE,
                        true,
                        Theme.SYSTEM
                ));


        SettingsEntity settingsToSave = new SettingsEntity(
                userId,
                request.likeNotification() != null ? request.likeNotification() : currentSettings.likeNotification(),
                request.commentNotification() != null ? request.commentNotification() : currentSettings.commentNotification(),
                request.friendNotification() != null ? request.friendNotification() : currentSettings.friendNotification(),
                request.messageNotification() != null ? request.messageNotification() : currentSettings.messageNotification(),

                request.whoCanSeePosts() != null ? VisibilityType.valueOf(request.whoCanSeePosts().name()) : currentSettings.whoCanSeePosts(),

                request.showOnlineStatus() != null ? request.showOnlineStatus() : currentSettings.showOnlineStatus(),

                request.theme() != null ? Theme.valueOf(request.theme().name()) : currentSettings.theme()
        );

        return ResponseEntity.ok(userSettingsUseCase.save(settingsToSave));
    }

}
