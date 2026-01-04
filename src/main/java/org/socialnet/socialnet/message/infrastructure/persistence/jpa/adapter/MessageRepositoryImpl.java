package org.socialnet.socialnet.message.infrastructure.persistence.jpa.adapter;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.socialnet.socialnet.message.core.model.Message;
import org.socialnet.socialnet.message.core.model.MessagesResponse;
import org.socialnet.socialnet.message.core.port.output.MessageRepository;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationEntity;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.MessageEntity;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.mapper.MessagePersistenceMapper;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.repository.ConversationJpaRepository;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.repository.MessageJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageJpaRepository jpaRepository;
    private final MessagePersistenceMapper mapper;
    private final ConversationJpaRepository conversationJpaRepository;
    private final MessagePersistenceMapper messagePersistenceMapper;
    private final MessageJpaRepository messageRepository;

    @Override
    @Transactional
    public Message save(Message message) {
        boolean isExists = conversationJpaRepository.existsById(message.getConversationId());
        if (!isExists) {
            throw new IllegalArgumentException("Conversation with id " + message.getConversationId() + " does not exist.");
        }

        var entity = mapper.toEntity(message);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public MessagesResponse getMessagesForConversation(String conversationOrUserId, String currentUserId, int page, int size) {
        Long conversationIdToFetch;

        try {
            conversationIdToFetch = Long.parseLong(conversationOrUserId);
        } catch (NumberFormatException e) {
            Optional<ConversationEntity> conversationOpt = conversationJpaRepository
                    .findPrivateConversationBetweenUsers(currentUserId, conversationOrUserId);

            if (conversationOpt.isPresent()) {
                conversationIdToFetch = conversationOpt.get().getId();
            } else {
                log.warn("No conversation found between users {} and {}. Returning empty message list.", currentUserId, conversationOrUserId);
                return new MessagesResponse(Collections.emptyList(), 0);
            }
        }

        if (!conversationJpaRepository.existsByIdAndParticipantsUserId(conversationIdToFetch, currentUserId)) {
            throw new AccessDeniedException("You are not a participant of this conversation.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<MessageEntity> messageEntitiesPage = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationIdToFetch, pageable);

        List<Message> messages = messageEntitiesPage.getContent().stream()
                .map(messagePersistenceMapper::toDomain)
                .toList();

        log.info("Fetched {} messages for conversationId {}. Total count: {}", messages.size(), conversationIdToFetch, messageEntitiesPage.getTotalElements());

        return new MessagesResponse(
                messages,
                messageEntitiesPage.getTotalElements()
        );
    }

    @Override
    public List<Message> searchMessages(Long conversationId, String userId, String query) {
        return messageRepository.searchByText(Long.valueOf(conversationId), userId, query).stream()
                .map(messagePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Message> findMessagesAround(Long conversationId, String userId, Long messageId, int limit) {
        List<MessageEntity> entities = messageRepository.findMessagesAroundId(conversationId, messageId);

        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> loadMessagesRelative(Long conversationId, String userId, Long messageId, String direction, int limit) {
        if (!conversationJpaRepository.existsByIdAndParticipantsUserId(conversationId, userId)) {
            log.warn("User {} is not participant of chat {}", userId, conversationId);
            return Collections.emptyList();
        }

        var cursorMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id " + messageId));

        if (!cursorMessage.getConversation().getId().equals(conversationId)) {
            throw new IllegalArgumentException("Message does not belong to this conversation");
        }

        List<MessageEntity> entities;
        Pageable pageable = PageRequest.of(0, limit);

        if ("OLDER".equalsIgnoreCase(direction)) {
            entities = messageRepository.findBefore(conversationId, cursorMessage.getCreatedAt(), pageable);
        } else if ("NEWER".equalsIgnoreCase(direction)) {
            entities = messageRepository.findAfter(conversationId, cursorMessage.getCreatedAt(), pageable);
        } else {
            throw new IllegalArgumentException("Invalid direction: " + direction);
        }

        return entities.stream()
                .map(messagePersistenceMapper::toDomain)
                .toList();
    }


}