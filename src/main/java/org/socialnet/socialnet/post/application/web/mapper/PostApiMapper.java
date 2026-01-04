package org.socialnet.socialnet.post.application.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.socialnet.socialnet.post.application.web.dto.CreatePostRequest;
import org.socialnet.socialnet.post.application.web.dto.PostsFeedResponse;
import org.socialnet.socialnet.post.core.model.Post;
import org.socialnet.socialnet.post.core.model.PostSummary;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostApiMapper {

    default Post toDomain(CreatePostRequest request, String authorId, List<String> fileUrls) {
        Post.Attachment attachment = null;
        if (request.attachmentType() != null) {
            attachment = switch (request.attachmentType().toUpperCase()) {
                case "IMAGE" -> new Post.ImageAttachment(fileUrls);
                case "VIDEO" -> {
                    String videoUrl = fileUrls.isEmpty() ? null : fileUrls.get(0);
                    yield new Post.VideoAttachment(videoUrl, null);
                }
                case "POLL" -> {
                    var options = request.pollOptions().stream()
                            .map(text -> new Post.PollOption(text, 0, 0L))
                            .toList();
                    yield new Post.PollAttachment(request.pollQuestion(), options);
                }
                default -> null;
            };
        }
        return Post.createNew(authorId, request.content(), attachment);
    }

    List<PostsFeedResponse> toResponseList(List<PostSummary> posts);

    @Mapping(target = "stats.likes", source = "likesCount")
    @Mapping(target = "stats.comments", source = "commentsCount")
    @Mapping(target = "stats.reposts", source = "repostsCount")

    @Mapping(target = "attachment", expression = "java(mapAttachmentDetails(summary.attachment(), summary.userVotedOptionId()))")
    PostsFeedResponse toResponse(PostSummary summary);

    PostsFeedResponse.AuthorDto mapAuthor(PostSummary.AuthorInfo author);

    default Object mapAttachmentDetails(Post.Attachment attachment, Long userVotedOptionId) {
        if (attachment == null) return null;

        return switch (attachment) {
            case Post.ImageAttachment img -> img;
            case Post.VideoAttachment vid -> vid;
            case Post.PollAttachment poll -> {
                List<PollOptionDto> optionDtos = poll.options().stream()
                        .map(opt -> new PollOptionDto(
                                opt.id(),
                                opt.text(),
                                opt.votes(),
                                userVotedOptionId != null && userVotedOptionId.equals(opt.id())
                        ))
                        .toList();
                yield new PollAttachmentDto(poll.question(), optionDtos);
            }
        };
    }

    record PollAttachmentDto(String question, List<PollOptionDto> options) {}
    record PollOptionDto(long id, String text, int votes, boolean isSelected) {}
}