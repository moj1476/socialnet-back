package org.socialnet.socialnet.message.core.port.input;

import org.socialnet.socialnet.message.core.model.Message;
import org.socialnet.socialnet.message.core.model.MessagesResponse;

import java.util.List;

public interface MessageUseCase {
    MessagesResponse getMessagesForConversation(String conversationId, String userId, int page, int size);
    List<Message> searchMessages(Long conversationId, String userId, String query);
    List<Message> findMessagesAround(Long conversationId, String userId, Long messageId, int limit);
    List<Message> loadMessagesRelative(Long conversationId, String userId, Long messageId, String direction, int limit);
}
