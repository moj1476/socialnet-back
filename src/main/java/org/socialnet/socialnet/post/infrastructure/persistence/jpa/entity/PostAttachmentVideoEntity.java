package org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_attachments_video")
@Getter
@Setter
@NoArgsConstructor
public class PostAttachmentVideoEntity {

    @Id
    @Column(name = "post_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Column(name = "video_url" ,nullable = false)
    private String video;

    @Column(name = "thumbnail_url")
    private String thumbnail;

    @Column(name = "duration_seconds")
    private int duration;

}
