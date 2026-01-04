package org.socialnet.socialnet.user.core.port.input;

import java.io.InputStream;

public interface ChangeAvatarUseCase {
    UpdateAvatarResult handle(UpdateAvatarCommand command);

    record UpdateAvatarCommand(
            String userId,
            FileToUpload avatarFile
    ) {}

    record FileToUpload(
            String fileName,
            String contentType,
            long size,
            InputStream contentStream
    ) {}

    record UpdateAvatarResult(String newAvatarUrl) {}
}
