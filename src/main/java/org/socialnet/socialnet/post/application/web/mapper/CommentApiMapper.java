package org.socialnet.socialnet.post.application.web.mapper;

import org.mapstruct.Mapper;
import org.socialnet.socialnet.post.application.web.dto.CommentResponse;
import org.socialnet.socialnet.post.core.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentApiMapper {

    CommentResponse toResponse(Comment comment);

    List<CommentResponse> toResponseList(List<Comment> comments);

    CommentResponse.AuthorDto mapAuthor(Comment.AuthorInfo author);
}