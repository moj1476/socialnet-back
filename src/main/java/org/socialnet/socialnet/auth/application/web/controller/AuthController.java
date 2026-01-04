package org.socialnet.socialnet.auth.application.web.controller;

import jakarta.validation.Valid;
import org.socialnet.socialnet.auth.application.web.dto.*;
import org.socialnet.socialnet.auth.application.web.mapper.AuthApiMapper;
import org.socialnet.socialnet.auth.core.port.input.SecurityPortUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final SecurityPortUseCase securityPortUseCase;
    private final AuthApiMapper authApiMapper;

    public AuthController(SecurityPortUseCase securityPortUseCase, AuthApiMapper authApiMapper) {
        this.securityPortUseCase = securityPortUseCase;
        this.authApiMapper = authApiMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var tokens = securityPortUseCase.login(loginRequest.login(), loginRequest.password());
        var tokenResponse = authApiMapper.toTokenResponse(tokens);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest regRequest) {
        var tokens = securityPortUseCase.registerNewUser(regRequest.login(), regRequest.email(), regRequest.password());
        var tokenResponse = authApiMapper.toTokenResponse(tokens);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        var tokens = securityPortUseCase.refresh(refreshRequest.refreshToken());
        var tokenResponse = authApiMapper.toTokenResponse(tokens);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/check")
    public ResponseEntity<Void> check() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest logoutRequest, @AuthenticationPrincipal Jwt jwt) {
        securityPortUseCase.logout(jwt.getTokenValue(), logoutRequest.refreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/change")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        securityPortUseCase.changePassword(
                jwt.getSubject(),
                changePasswordRequest.newPassword()
        );
        return ResponseEntity.ok().build();
    }
}
