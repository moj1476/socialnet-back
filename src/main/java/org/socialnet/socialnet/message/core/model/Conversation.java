package org.socialnet.socialnet.message.core.model;

import java.time.Instant;
import java.util.List;

public class Conversation {
    Long id;
    ConversationType type;
    String title;
    String lastMessage;
    String avatarUrl;
    Instant createdAt;
    List<Message> messages;
    List<ConversationParticipant> participants;
    boolean isOnline;

    public Conversation(Long id, ConversationType type,
                        String title, Instant createdAt, List<Message> messages,
                        List<ConversationParticipant> participants,
                        String avatarUrl, String lastMessage, boolean isOnline) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.createdAt = createdAt;
        this.messages = messages;
        this.participants = participants;
        this.avatarUrl = avatarUrl;
        this.lastMessage = lastMessage;
        this.isOnline = isOnline;
    }

    public static Conversation createNew(ConversationType type, String title,
                                         List<ConversationParticipant> participants,
                                         String avatarUrl, String lastMessage,
                                         boolean isOnline) {
        if (type == null || participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("Invalid conversation data");
        }
        return new Conversation(null, type, title, Instant.now(),
                List.of(), participants, avatarUrl, lastMessage, isOnline);
    }

    public static Conversation fromData(Long id, ConversationType type, String title, Instant createdAt,
                                        List<Message> messages,
                                        List<ConversationParticipant> participants,
                                        String avatarUrl, String lastMessage,
                                        boolean isOnline) {
        return new Conversation(id, type, title, createdAt, messages,
                participants, avatarUrl, lastMessage, isOnline);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConversationType getType() {
        return type;
    }

    public void setType(ConversationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<ConversationParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ConversationParticipant> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }
}
