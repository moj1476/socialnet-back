package org.socialnet.socialnet.message.application.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.socialnet.socialnet.message.application.web.dto.MessageDto;
import org.socialnet.socialnet.message.application.web.dto.MessageResponse;
import org.socialnet.socialnet.message.application.web.dto.SendMessageDto;
import org.socialnet.socialnet.message.core.model.Message;
import org.socialnet.socialnet.message.core.port.input.SendMessageUseCase;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageApiMapper {

    @Mapping(target = "type", constant = "TEXT")
    SendMessageUseCase.SendMessageCommand toCommand(SendMessageDto dto, String senderId, Long conversationId);

    // @Mapping(source = "read", target = "isRead")
    MessageDto toDto(Message message);

    List<MessageDto> toDto(List<Message> messages);

    MessageResponse toMessageResponse(List<MessageDto> messages, long totalCount);
}