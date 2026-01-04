package org.socialnet.socialnet.post.core.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Post {

    private final Long id;
    private final String authorId;
    private String content;
    private Attachment attachment;
    private boolean isDeleted;
    private final Instant createdAt;
    private Instant updatedAt;

    private Post(Long id, String authorId, String content, Attachment attachment, boolean isDeleted, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.attachment = attachment;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static Post createNew(String authorId, String content, Attachment attachment) {
        if (authorId.isEmpty()) {
            throw new IllegalArgumentException("Author ID must be positive.");
        }
        return new Post(null, authorId, content, attachment, false, Instant.now(), Instant.now());
    }


    public static Post fromData(Long id, String authorId, String content, Attachment attachment, boolean isDeleted, Instant createdAt, Instant updatedAt) {
        return new Post(id, authorId, content, attachment, isDeleted, createdAt, updatedAt);
    }


    public void markAsDeleted() {
        if (this.isDeleted) {
            return;
        }
        this.isDeleted = true;
        this.updatedAt = Instant.now();
    }


    public void editContent(String newContent) {
        if (this.isDeleted) {
            throw new IllegalStateException("Cannot edit a deleted post.");
        }
        this.content = Objects.requireNonNull(newContent);
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public Attachment getAttachment() { return attachment; }
    public boolean isDeleted() { return isDeleted; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }



    public sealed interface Attachment permits ImageAttachment, VideoAttachment, PollAttachment {}

    public record ImageAttachment(List<String> imageUrls) implements Attachment {}

    public record VideoAttachment(String videoUrl, String thumbnailUrl) implements Attachment {}
    public record PollAttachment(String question, List<PollOption> options) implements Attachment {}
    public record PollOption(String text, int votes, long id) {}
}
