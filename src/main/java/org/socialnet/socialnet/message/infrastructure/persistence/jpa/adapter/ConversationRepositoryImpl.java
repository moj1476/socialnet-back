package org.socialnet.socialnet.message.infrastructure.persistence.jpa.adapter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.socialnet.socialnet.message.core.model.*;
import org.socialnet.socialnet.message.core.port.output.ConversationRepository;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationEntity;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationParticipantEntity;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.ConversationTypeDb;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity.MessageEntity;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.mapper.AdvancedConvPersistenceMapper;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.mapper.ConversationParticipantMapper;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.mapper.ConversationPersistenceMapper;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.repository.ConversationInfo;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.repository.ConversationJpaRepository;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.repository.MessageJpaRepository;
import org.socialnet.socialnet.user.core.model.CurrentProfile;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.adapter.ProfileRepositoryImpl;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.adapter.UserRepositoryImpl;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.ProfileEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository.ProfileJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class ConversationRepositoryImpl implements ConversationRepository {

    private final ConversationJpaRepository conversationJpaRepository;
    private final ConversationPersistenceMapper conversationPersistenceMapper;
    private final ConversationParticipantMapper conversationParticipantMapper;
    private final ProfileRepositoryImpl profileRepository;
    private final AdvancedConvPersistenceMapper advancedConvPersistenceMapper;
    private final MessageJpaRepository messageJpaRepository;
    private final UserRepositoryImpl userRepository;

    private static final int INITIAL_PAGE_SIZE = 20;

    @Override
    public List<Conversation> getConversations(String userId) {
        List<ConversationInfo> projections = conversationJpaRepository.findConversationsForUser(userId);

        return projections.stream().map(projection -> {
            String title;
            String avatarUrl;
            String lastMessage;
            Boolean isOnline = false;

            if (projection.getType() == ConversationTypeDb.PRIVATE) {
                String firstName = projection.getParticipantFirstName();
                String lastName = projection.getParticipantLastName();
                title = (firstName != null ? firstName : "") + " " + (lastName != null ? " " + lastName : "").trim();
                if (title.isBlank()) {
                    title = "Пользователь";
                }
                avatarUrl = projection.getParticipantAvatarUrl();
                isOnline = userRepository.isUserOnline(projection.getParticipantUserId());
            } else {
                title = projection.getTitle();
                avatarUrl = null;
            }

            if (projection.getLastMessageContent() != null) {
                String senderId = projection.getLastMessageSenderId();
                if (senderId != null && senderId.equals(userId)) {
                    lastMessage = "Вы: " + projection.getLastMessageContent();
                } else {
                    String senderFirstName = projection.getLastMessageSenderFirstName();
                    String prefix = (senderFirstName != null && !senderFirstName.isBlank()) ? senderFirstName + ": " : "Собеседник: ";
                    lastMessage = prefix + projection.getLastMessageContent();
                }
            } else {
                lastMessage = "Нет сообщений";
            }

            return new Conversation(
                    projection.getConversationId(),
                    ConversationType.valueOf(projection.getType().toString()),
                    title,
                    projection.getLastMessageTimestamp(),
                    List.of(),
                    List.of(),
                    avatarUrl,
                    lastMessage,
                    isOnline
            );

        }).toList();
    }

    @Override
    public Conversation save(Conversation conversation) {
        return null;
    }

    @Override
    @Transactional
    public Conversation create(List<String> participantIds, String title, String firstMessageContent, String creatorId) {
        if (participantIds != null && participantIds.size() == 2) {
            String user1 = participantIds.get(0);
            String user2 = participantIds.get(1);

            Optional<ConversationEntity> existingConversation = conversationJpaRepository
                    .findPrivateConversationByTwoUsers(user1, user2);

            if (existingConversation.isPresent()) {
                throw new IllegalStateException("Диалог между этими пользователями уже существует. Используйте веб-сокеты для отправки сообщений.");
            }
        }

        if (participantIds == null || participantIds.isEmpty() || creatorId == null || creatorId.isBlank()) {
            throw new IllegalArgumentException("Недостаточно данных для создания чата.");
        }
        if (!participantIds.contains(creatorId)) {
            throw new IllegalArgumentException("Создатель чата должен быть в списке участников.");
        }

        ConversationEntity conversationEntity = new ConversationEntity();
        conversationEntity.setTitle(title);
        conversationEntity.setType(participantIds.size() > 2 ? ConversationTypeDb.GROUP : ConversationTypeDb.PRIVATE);

        Set<ConversationParticipantEntity> participantEntities = new HashSet<>();
        for (String userId : participantIds) {
            ConversationParticipantEntity participantEntity = new ConversationParticipantEntity();
            participantEntity.setUserId(userId);
            participantEntity.setConversation(conversationEntity);
            participantEntities.add(participantEntity);
        }
        conversationEntity.setParticipants(participantEntities);

        var savedConversation = conversationJpaRepository.save(conversationEntity);

        if (firstMessageContent != null && !firstMessageContent.isBlank()) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setContent(firstMessageContent);
            messageEntity.setSenderId(creatorId);
            messageEntity.setConversation(savedConversation);
            messageJpaRepository.save(messageEntity);
        }

        return conversationPersistenceMapper.toDomain(savedConversation);
    }

    @Override
    public void deleteById(Long conversationId) {

    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getConversationParticipants(Long conversationId) {
        return conversationJpaRepository.findById(conversationId)
                .map(conversationEntity -> conversationEntity.getParticipants().stream()
                        .map(ConversationParticipantEntity::getUserId)
                        .toList())
                .orElse(List.of());
    }

    @Override
    @Transactional
    public Call getConversationParticipantsToCall(Long conversationId, String userId) {
        Optional<ConversationEntity> conversationOpt = conversationJpaRepository.findById(conversationId);

        if (conversationOpt.isEmpty()) {
            throw new IllegalArgumentException("Conversation not found");
        }

        ConversationEntity conversationEntity = conversationOpt.get();

        List<Call.Participant> participants = conversationEntity.getParticipants().stream()
                .map(participantEntity -> {
                    String participantId = participantEntity.getUserId();
                    CurrentProfile profile = profileRepository.findById(participantId).orElse(null);
                    String username = profile != null
                            ? (profile.firstName() != null ? profile.firstName() : "") + " " +
                              (profile.lastName() != null ? profile.lastName() : "")
                            : "Неизвестный";
                    String avatarUrl = profile != null ? profile.avatarUrl() : null;
                    return new Call.Participant(participantId, username.trim(), avatarUrl);
                }).toList(); //TODO: тут еще проверять будем включены ли уведы в настройках
        String title = conversationEntity.getTitle();
        String callerName = participants.stream()
                .filter(p -> p.userId().equals(userId))
                .map(Call.Participant::username)
                .findFirst()
                .orElse("Неизвестный");
        return new Call(
                participants,
                title,
                callerName,
                userId,
                profileRepository.findById(userId)
                        .map(CurrentProfile::avatarUrl)
                        .orElse(null)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DetailedConversation> getConversationByIdAndUserId(String potentialId, String currentUserId) {
        try {
            Long convId = Long.parseLong(potentialId);
            Optional<ConversationEntity> entityOpt = conversationJpaRepository.findById(convId);

            if (entityOpt.isPresent()) {
                boolean isParticipant = entityOpt.get().getParticipants().stream()
                        .anyMatch(p -> p.getUserId().equals(currentUserId));

                if (isParticipant) {
                    return Optional.of(buildDetailedConversationFromEntity(entityOpt.get()));
                }
            }
        } catch (NumberFormatException ignored) {
        }

        Optional<ConversationEntity> privateConvOpt = conversationJpaRepository
                .findPrivateConversationBetweenUsers(currentUserId, potentialId);

        if (privateConvOpt.isPresent()) {
            return Optional.of(buildDetailedConversationFromEntity(privateConvOpt.get()));
        }

        Optional<CurrentProfile> profileOpt = profileRepository.findById(potentialId);

        return profileOpt.map(profile -> {
            String userName = (profile.firstName() != null ? profile.firstName() : "") + " " +
                    (profile.lastName() != null ? profile.lastName() : "");

            List<DetailedConversation.Participant> participants = List.of(
                    new DetailedConversation.Participant(currentUserId, "Вы"),
                    new DetailedConversation.Participant(profile.id(), userName.trim())
            );

            return new DetailedConversation(
                    null,
                    userName.trim(),
                    Instant.now().toString(),
                    Collections.emptyList(),
                    participants
            );
        });
    }

    @Override
    public void deleteConversation(Long conversationId, String userId) {
        if(!conversationJpaRepository.existsByIdAndParticipantsUserId(conversationId, userId)) {
            throw new IllegalArgumentException("Conversation not found");
        }

        conversationJpaRepository.deleteById(conversationId);
    }

    private DetailedConversation buildDetailedConversationFromEntity(ConversationEntity entity) {
        Pageable initialPage = PageRequest.of(0, INITIAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MessageEntity> messagesPage = messageJpaRepository.findByConversationIdOrderByCreatedAtDesc(entity.getId(), initialPage);

        Set<String> userIdsToFetch = entity.getParticipants().stream()
                .map(ConversationParticipantEntity::getUserId)
                .collect(Collectors.toSet());
        messagesPage.getContent().forEach(m -> userIdsToFetch.add(m.getSenderId()));

        Map<String, String> userNamesMap = profileRepository.findNamesByIds(userIdsToFetch);

        List<DetailedConversation.Participant> participants = entity.getParticipants().stream()
                .map(p -> new DetailedConversation.Participant(
                        p.getUserId(),
                        userNamesMap.getOrDefault(p.getUserId(), "Неизвестный")
                )).toList();

        List<DetailedConversation.Message> messages = messagesPage.getContent().stream()
                .map(m -> new DetailedConversation.Message(
                        m.getId(),
                        m.getSenderId(),
                        userNamesMap.getOrDefault(m.getSenderId(), "Неизвестный"),
                        m.getContent(),
                        m.getType().name(),
                        m.getCreatedAt().toString()
                )).toList();

        String title = entity.getTitle();
        if (entity.getType() == ConversationTypeDb.PRIVATE && participants.size() == 2) {
            title = "";
        }

        return new DetailedConversation(
                entity.getId(),
                title,
                entity.getCreatedAt().toString(),
                messages,
                participants
        );
    }



}
