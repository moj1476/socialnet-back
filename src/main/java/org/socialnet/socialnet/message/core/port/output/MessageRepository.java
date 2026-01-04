package org.socialnet.socialnet.message.core.port.output;

import org.socialnet.socialnet.message.core.model.Message;
import org.socialnet.socialnet.message.core.model.MessagesResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageRepository {
    Message save(Message message);
    MessagesResponse getMessagesForConversation(String conversationId, String userId, int page, int size);
    List<Message> searchMessages(Long conversationId, String userId, String query);
    List<Message> findMessagesAround(Long conversationId, String userId, Long messageId, int limit);
    List<Message> loadMessagesRelative(Long conversationId, String userId, Long messageId, String direction, int limit);
}