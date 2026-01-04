package org.socialnet.socialnet.message.core.port.output;

import org.socialnet.socialnet.message.core.model.Call;
import org.socialnet.socialnet.message.core.model.Conversation;
import org.socialnet.socialnet.message.core.model.ConversationParticipant;
import org.socialnet.socialnet.message.core.model.DetailedConversation;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository {
    // Optional<Conversation> findPrivateConversationBetween(String userId1, String userId2);
    List<Conversation> getConversations(String userId);
    Conversation save(Conversation conversation);
    Conversation create(List<String> participantIds, String title, String firstMessageContent, String creatorId);
    void deleteById(Long conversationId);
    List<String> getConversationParticipants(Long conversationId);
    Call getConversationParticipantsToCall(Long conversationId, String userId);
    Optional<DetailedConversation> getConversationByIdAndUserId(String conversationId, String userId);
    void deleteConversation(Long conversationId, String userId);
}