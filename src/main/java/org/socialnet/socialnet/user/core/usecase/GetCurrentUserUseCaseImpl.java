package org.socialnet.socialnet.user.core.usecase;

import org.socialnet.socialnet.shared.core.port.output.FileStoragePort;
import org.socialnet.socialnet.user.core.model.User;
import org.socialnet.socialnet.user.core.port.input.GetCurrentUserUseCase;
import org.socialnet.socialnet.user.core.port.output.ProfileRepository;
import org.socialnet.socialnet.user.core.port.output.UserRepository;

public class GetCurrentUserUseCaseImpl implements GetCurrentUserUseCase {
    private final UserRepository userRepository;

    public GetCurrentUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User handle(String userId) {

        return userRepository.findById(userId);
    }
}
