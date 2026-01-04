package org.socialnet.socialnet.message.core.usecase;

import org.socialnet.socialnet.message.core.model.*;
import org.socialnet.socialnet.message.core.port.input.ConversationUseCase;
import org.socialnet.socialnet.message.core.port.output.ConversationRepository;

import java.util.List;
import java.util.Optional;

public class ConversationUseCaseImpl implements ConversationUseCase {

    private final ConversationRepository conversationRepository;

    public ConversationUseCaseImpl(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @Override
    public List<Conversation> getConversations(String userId) {
        return conversationRepository.getConversations(userId);
    }

    @Override
    public void deleteConversation(Long conversationId, String userId) {
        conversationRepository.deleteConversation(conversationId, userId);
    }

    @Override
    public void createConversation(List<String> participantIds, String title, String firstMessageContent, String creatorId) {
        conversationRepository.create(participantIds, title, firstMessageContent, creatorId);
    }

    @Override
    public List<String> getConversationParticipants(Long conversationId) {
        return conversationRepository.getConversationParticipants(conversationId);
    }

    @Override
    public Call getConversationParticipantsToCall(Long conversationId, String userId) {
        return conversationRepository.getConversationParticipantsToCall(conversationId, userId);
    }

    @Override
    public Optional<DetailedConversation> getConversationByIdAndUserId(String conversationId, String userId) {
        var conversation = conversationRepository.getConversationByIdAndUserId(conversationId, userId);
        return conversation;
    }


}
