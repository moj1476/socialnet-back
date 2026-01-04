package org.socialnet.socialnet.message.core.model;

import java.time.Instant;

public class Message {

    private final Long id;
    private final Long conversationId;
    private final String senderId;
    private final String content;
    private final MessageType type;
    // private boolean isRead;
    private final Instant createdAt;

    public Message(Long id, Long conversationId, String senderId, String content, MessageType type, Instant createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
    }

    public static Message createNew(Long conversationId, String senderId, String content, MessageType type) {
        if (conversationId == null || senderId == null || senderId.isBlank() || content == null || content.isBlank()) {
            throw new IllegalArgumentException("Invalid message data");
        }
        return new Message(null, conversationId, senderId, content, type, Instant.now());
    }

    public static Message fromData(Long id, Long conversationId, String senderId, String content, MessageType type, Instant createdAt) {
        return new Message(id, conversationId, senderId, content, type, createdAt);
    }


    public Long getId() {
        return id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }

    // public boolean isRead() { return isRead; }

    public Instant getCreatedAt() {
        return createdAt;
    }
}