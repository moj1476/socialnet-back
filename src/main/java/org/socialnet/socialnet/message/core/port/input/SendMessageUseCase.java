package org.socialnet.socialnet.message.core.port.input;

import org.socialnet.socialnet.message.core.model.Message;
import org.socialnet.socialnet.message.core.model.MessageType;

public interface SendMessageUseCase {

    Message handle(SendMessageCommand command);

    record SendMessageCommand(
            String senderId,
            Long conversationId,
            String content,
            MessageType type
    ) {
        public SendMessageCommand {
            if (senderId == null || senderId.isBlank()) {
                throw new IllegalArgumentException("Sender ID cannot be blank.");
            }
            if (conversationId == null) {
                throw new IllegalArgumentException("Conversation ID cannot be null.");
            }
            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("Message content cannot be blank.");
            }
            if (type == null) {
                type = MessageType.TEXT;
            }
        }
    }
}