package org.socialnet.socialnet.message.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.socialnet.socialnet.message.core.model.Conversation;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationEntity;

@Mapper(componentModel = "spring")
public interface ConversationPersistenceMapper {

    ConversationEntity toEntity(Conversation conversation);

    Conversation toDomain(ConversationEntity entity);


}
