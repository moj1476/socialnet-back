package org.socialnet.socialnet.message.application.web.controller;

import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.message.application.web.dto.CallTokenResponseDto;
import org.socialnet.socialnet.message.core.port.input.GetCallTokenUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/calls")
@RequiredArgsConstructor
public class CallController {

    private final GetCallTokenUseCase getCallTokenUseCase;

    @GetMapping("/token")
    public ResponseEntity<CallTokenResponseDto> getToken(
            @RequestParam String chatId,
            @AuthenticationPrincipal Jwt principal
    ) {
        String userId = principal.getSubject();

        String liveKitToken = getCallTokenUseCase.execute(userId, chatId);

        return ResponseEntity.ok(
                new CallTokenResponseDto(liveKitToken)
        );
    }
}