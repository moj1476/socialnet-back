package org.socialnet.socialnet.auth.core.usecase;

import org.socialnet.socialnet.auth.core.model.AuthenticatedUser;
import org.socialnet.socialnet.auth.core.model.JwtTokens;
import org.socialnet.socialnet.auth.core.port.input.SecurityPortUseCase;
import org.socialnet.socialnet.auth.core.port.output.AuthUseCase;
import org.socialnet.socialnet.user.core.port.output.UserRepository;

import java.util.Optional;

public class SecurityPortUseCaseImpl implements SecurityPortUseCase {
    private final AuthUseCase authUseCase;
    private final UserRepository userRepository;

    public SecurityPortUseCaseImpl(AuthUseCase authUseCase, UserRepository userRepository) {
        this.authUseCase = authUseCase;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<AuthenticatedUser> getCurrentUser() {
        return Optional.empty();
    }

    @Override
    public JwtTokens registerNewUser(String login, String email, String password) {
        var tokens = authUseCase.registerNewUser(login, email, password);
        var user = authUseCase.getUserByToken(tokens.accessToken());
        userRepository.findOrCreate(user.get().getId(), user.get().getLogin(), user.get().getEmail());
        return tokens;
    }

    @Override
    public JwtTokens login(String login, String password) {
        return authUseCase.login(login, password);
    }

    @Override
    public JwtTokens refresh(String refreshToken) {
        return authUseCase.refreshToken(refreshToken);
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        authUseCase.logout(accessToken, refreshToken);
    }

    @Override
    public void changePassword(String userId, String newPassword) {
        authUseCase.changePassword(userId, newPassword);
    }
}
