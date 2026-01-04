package org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "post_attachments_poll_option")
@Getter
@Setter
@NoArgsConstructor
public class PostAttachmentPollOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_post_id")
    private PostAttachmentPollEntity poll;

    @Column(name = "option_text", nullable = false)
    private String option;

    @Formula("(SELECT COUNT(*) FROM post_attachments_poll_vote v WHERE v.option_id = id)")
    private int votesCount;
}