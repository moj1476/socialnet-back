package org.socialnet.socialnet.auth.core.port.output;

import org.socialnet.socialnet.auth.core.model.AuthenticatedUser;
import org.socialnet.socialnet.auth.core.model.JwtTokens;

import java.util.Optional;

public interface AuthUseCase {

    JwtTokens registerNewUser(String login, String email, String password);

    JwtTokens login(String login, String password);

    Optional<AuthenticatedUser> getUserByToken(String token);

    JwtTokens refreshToken(String token);

    void logout(String accessToken, String refreshToken);

    void changePassword(String userId, String newPassword);
}
