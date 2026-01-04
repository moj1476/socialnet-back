package org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "post_attachments_poll_vote")
@Getter
@Setter
@NoArgsConstructor
public class PollVoteEntity {

    @EmbeddedId
    private PollVoteId id;

    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public PollVoteEntity(Long pollPostId, String userId, Long optionId) {
        this.id = new PollVoteId(pollPostId, userId);
        this.optionId = optionId;
    }
}