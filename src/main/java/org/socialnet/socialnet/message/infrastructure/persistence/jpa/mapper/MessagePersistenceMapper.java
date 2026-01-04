package org.socialnet.socialnet.message.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.socialnet.socialnet.message.core.model.Message;
import org.socialnet.socialnet.message.core.model.MessageType;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationEntity;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.MessageEntity;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.MessageTypeDb;

@Mapper(componentModel = "spring")
public interface MessagePersistenceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "read", ignore = true)
    @Mapping(target = "conversation", source = "conversationId", qualifiedByName = "mapConversationIdToEntity")
    MessageEntity toEntity(Message message);


    @Mapping(target = "conversationId", source = "conversation.id")
    Message toDomain(MessageEntity entity);

    @Named("mapConversationIdToEntity")
    default ConversationEntity mapConversationIdToEntity(Long conversationId) {
        if (conversationId == null) {
            return null;
        }
        ConversationEntity conversation = new ConversationEntity();
        conversation.setId(conversationId);
        return conversation;
    }

    default MessageTypeDb toMessageTypeDb(MessageType type) {
        return MessageTypeDb.valueOf(type.name());
    }

    default MessageType toMessageType(MessageTypeDb type) {
        return MessageType.valueOf(type.name());
    }
}