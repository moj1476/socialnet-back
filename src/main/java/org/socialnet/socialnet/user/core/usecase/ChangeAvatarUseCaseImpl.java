package org.socialnet.socialnet.user.core.usecase;

import org.socialnet.socialnet.shared.core.port.output.FileStoragePort;
import org.socialnet.socialnet.user.core.port.input.ChangeAvatarUseCase;
import org.socialnet.socialnet.user.core.port.output.ProfileRepository;
import org.socialnet.socialnet.user.core.port.output.UserRepository;

public class ChangeAvatarUseCaseImpl implements ChangeAvatarUseCase {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FileStoragePort fileStoragePort;

    public ChangeAvatarUseCaseImpl(ProfileRepository profileRepository, UserRepository userRepository, FileStoragePort fileStoragePort) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.fileStoragePort = fileStoragePort;
    }

    @Override
    public UpdateAvatarResult handle(UpdateAvatarCommand command) {
        if (command.avatarFile() == null) {
            throw new IllegalArgumentException("Файл аватара не может быть пустым.");
        }

        var profile = profileRepository.findById(command.userId());
        String fileToRemove = null;
        if(profile.isPresent()) {
            fileToRemove = profile.get().avatarUrl();
        }

        String avatarUrl = fileStoragePort.save(
                command.avatarFile().contentStream(),
                command.avatarFile().fileName(),
                "avatars",
                fileToRemove
        );
        profileRepository.updateAvatarUrl(command.userId(), avatarUrl);

        return new UpdateAvatarResult(avatarUrl);
    }
}
