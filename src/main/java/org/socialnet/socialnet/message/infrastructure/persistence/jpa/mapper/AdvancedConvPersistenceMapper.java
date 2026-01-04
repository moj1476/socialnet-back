package org.socialnet.socialnet.message.infrastructure.persistence.jpa.mapper;

import org.socialnet.socialnet.message.core.model.DetailedConversation;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationEntity;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.MessageEntity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class AdvancedConvPersistenceMapper {
    public DetailedConversation toDetailedConversation(ConversationEntity entity, Map<String, String> userNamesMap) {
        if (entity == null) {
            return null;
        }

        List<DetailedConversation.Participant> participants = entity.getParticipants().stream()
                .map(p -> new DetailedConversation.Participant(
                        p.getUserId(),
                        userNamesMap.getOrDefault(p.getUserId(), "Неизвестный пользователь")
                ))
                .toList();

        List<DetailedConversation.Message> messages = entity.getMessages().stream()
                .sorted(Comparator.comparing(MessageEntity::getCreatedAt))
                .map(m -> new DetailedConversation.Message(
                        m.getId(),
                        m.getSenderId(),
                        userNamesMap.getOrDefault(m.getSenderId(), "Неизвестный пользователь"),
                        m.getContent(),
                        m.getType().name(),
                        m.getCreatedAt().toString()
                ))
                .toList();

        return new DetailedConversation(
                entity.getId(),
                entity.getTitle(),
                entity.getCreatedAt().toString(),
                messages,
                participants
        );
    }

}