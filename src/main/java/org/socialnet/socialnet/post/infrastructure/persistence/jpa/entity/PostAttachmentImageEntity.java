package org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_attachments_image")
@Getter
@Setter
@NoArgsConstructor
public class PostAttachmentImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Column(name = "image_url" ,nullable = false)
    private String image;

    @Column(name = "height")
    private int height;

    @Column(name = "width")
    private int width;

    @Column(name = "\"order\"")
    private int order;
}