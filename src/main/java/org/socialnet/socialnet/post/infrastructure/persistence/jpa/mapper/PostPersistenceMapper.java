package org.socialnet.socialnet.post.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ObjectFactory;
import org.socialnet.socialnet.post.core.model.Post;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.PostAttachmentTypeDb;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.PostEntity;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.PostAttachmentImageEntity;

import java.util.Collections;

@Mapper(componentModel = "spring")
public interface PostPersistenceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachmentType", source = "attachment", qualifiedByName = "mapAttachmentType")
    PostEntity toEntity(Post post);

    @ObjectFactory
    default Post createPost(PostEntity entity) {
        Post.Attachment attachment = mapAttachmentToDomain(entity);
        return Post.fromData(
                entity.getId(),
                entity.getAuthorId(),
                entity.getContent(),
                attachment,
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    Post toDomain(PostEntity entity);

    default Post.Attachment mapAttachmentToDomain(PostEntity entity) {
        if (entity.getAttachmentType() == null) {
            return null;
        }
        return switch (entity.getAttachmentType()) {
            case IMAGE -> {
                var urls = entity.getImageAttachments() == null
                        ? Collections.<String>emptyList()
                        : entity.getImageAttachments().stream().map(PostAttachmentImageEntity::getImage).toList();
                yield new Post.ImageAttachment(urls);
            }
            case VIDEO -> new Post.VideoAttachment(entity.getVideoAttachment().getVideo(), entity.getVideoAttachment().getThumbnail());

            case POLL -> {
                var pollEntity = entity.getPollAttachment();
                var options = pollEntity.getOptions().stream()
                        .map(opt -> new Post.PollOption(
                                opt.getOption(),
                                opt.getVotesCount(),
                                opt.getId()
                        ))
                        .toList();
                yield new Post.PollAttachment(pollEntity.getQuestion(), options);
            }
            case TEXT -> null;
        };
    }

    @Named("mapAttachmentType")
    default PostAttachmentTypeDb mapAttachmentType(Post.Attachment attachment) {
        if (attachment == null) return PostAttachmentTypeDb.TEXT;
        return switch (attachment) {
            case Post.ImageAttachment _ -> PostAttachmentTypeDb.IMAGE;
            case Post.VideoAttachment _ -> PostAttachmentTypeDb.VIDEO;
            case Post.PollAttachment _ -> PostAttachmentTypeDb.POLL;
        };
    }
}