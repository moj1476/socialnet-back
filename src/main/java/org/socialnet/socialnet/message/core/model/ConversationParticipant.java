package org.socialnet.socialnet.message.core.model;

public class ConversationParticipant {
    Long id;
    String userId;

    public ConversationParticipant(Long id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public static ConversationParticipant fromData(Long id, String userId) {
        return new ConversationParticipant(id, userId);
    }

    public static ConversationParticipant createNew(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Invalid participant data");
        }
        return new ConversationParticipant(null, userId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
