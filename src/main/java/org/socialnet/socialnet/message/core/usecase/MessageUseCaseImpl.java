package org.socialnet.socialnet.message.core.usecase;


import org.socialnet.socialnet.message.core.model.Message;
import org.socialnet.socialnet.message.core.model.MessagesResponse;
import org.socialnet.socialnet.message.core.port.input.MessageUseCase;
import org.socialnet.socialnet.message.core.port.output.ConversationRepository;
import org.socialnet.socialnet.message.core.port.output.MessageRepository;

import java.util.List;
import java.util.Objects;

public class MessageUseCaseImpl implements MessageUseCase {
    private final MessageRepository messageRepository;

    public MessageUseCaseImpl(MessageRepository messageRepository) {
        this.messageRepository = Objects.requireNonNull(messageRepository);
    }

    @Override
    public MessagesResponse getMessagesForConversation(String conversationId, String userId, int page, int size) {
        return messageRepository.getMessagesForConversation(conversationId, userId, page, size);
    }

    @Override
    public List<Message> searchMessages(Long conversationId, String userId, String query) {
        return messageRepository.searchMessages(conversationId, userId, query);
    }

    @Override
    public List<Message> findMessagesAround(Long conversationId, String userId, Long messageId, int limit) {
        return messageRepository.findMessagesAround(conversationId, userId, messageId, limit);
    }

    @Override
    public List<Message> loadMessagesRelative(Long conversationId, String userId, Long messageId, String direction, int limit) {
        return messageRepository.loadMessagesRelative(conversationId, userId, messageId, direction, limit);
    }
}
