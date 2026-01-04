package org.socialnet.socialnet.auth.core.port.input;

import org.socialnet.socialnet.auth.core.model.AuthenticatedUser;
import org.socialnet.socialnet.auth.core.model.JwtTokens;

import java.util.Optional;

public interface SecurityPortUseCase {
    Optional<AuthenticatedUser> getCurrentUser();
    JwtTokens registerNewUser(String login, String email, String password);
    JwtTokens login(String login, String password);
    JwtTokens refresh(String refreshToken);
    void logout(String accessToken, String refreshToken);
    void changePassword(String userId, String newPassword);
}
