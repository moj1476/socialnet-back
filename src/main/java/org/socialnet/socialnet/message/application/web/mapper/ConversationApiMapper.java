package org.socialnet.socialnet.message.application.web.mapper;

import org.mapstruct.Mapper;
import org.socialnet.socialnet.message.application.web.dto.ConversationDto;
import org.socialnet.socialnet.message.application.web.dto.DetailedConversationDto;
import org.socialnet.socialnet.message.core.model.Conversation;
import org.socialnet.socialnet.message.core.model.DetailedConversation;

@Mapper(componentModel = "spring")
public interface ConversationApiMapper {

    ConversationDto toDto(Conversation conversation);

    DetailedConversationDto toDetailedDto(DetailedConversation detailedConversation);

}
