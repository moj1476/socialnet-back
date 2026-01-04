package org.socialnet.socialnet.message.infrastructure.persistence.jpa.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.socialnet.socialnet.message.core.model.Conversation;
import org.socialnet.socialnet.message.core.model.ConversationParticipant;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationEntity;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationParticipantEntity;

@Mapper(componentModel = "spring")
public interface ConversationParticipantMapper {

    ConversationParticipantEntity toEntity(ConversationParticipant conversation);

    ConversationParticipant toDomain(ConversationParticipantEntity entity);


}
