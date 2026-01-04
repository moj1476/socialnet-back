package org.socialnet.socialnet.message.core.port.input;

import org.socialnet.socialnet.message.core.model.*;

import java.util.List;
import java.util.Optional;

public interface ConversationUseCase {
    List<Conversation> getConversations(String userId);
    void deleteConversation(Long conversationId, String userId);
    void createConversation(List<String> participantIds, String title, String firstMessageContent, String creatorId);
    List<String> getConversationParticipants(Long conversationId);
    Call getConversationParticipantsToCall(Long conversationId, String userId);
    Optional<DetailedConversation> getConversationByIdAndUserId(String conversationId, String userId);

}
