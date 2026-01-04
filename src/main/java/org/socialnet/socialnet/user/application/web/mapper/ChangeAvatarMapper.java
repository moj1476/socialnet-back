package org.socialnet.socialnet.user.application.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.socialnet.socialnet.user.application.web.dto.ChangeAvatarResponse;
import org.socialnet.socialnet.user.core.port.input.ChangeAvatarUseCase.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface ChangeAvatarMapper {

    UpdateAvatarCommand toCommand(String userId, FileToUpload avatarFile);

    @Mapping(source = "originalFilename", target = "fileName")
    @Mapping(source = "inputStream", target = "contentStream")
    FileToUpload toFile(MultipartFile file) throws IOException;

    ChangeAvatarResponse toResponse(UpdateAvatarResult newAvatarUrl);

}
