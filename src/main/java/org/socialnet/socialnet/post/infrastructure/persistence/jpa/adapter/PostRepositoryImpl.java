package org.socialnet.socialnet.post.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.post.core.model.Post;
import org.socialnet.socialnet.post.core.model.PostSummary;
import org.socialnet.socialnet.post.core.model.TrendingTopic;
import org.socialnet.socialnet.post.core.port.output.PostRepository;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.*;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.mapper.PostPersistenceMapper;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.PollVoteJpaRepository;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.PostJpaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostJpaRepository postJpaRepository;
    private final PostPersistenceMapper mapper;
    private final PollVoteJpaRepository pollVoteJpaRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    @Transactional
    @CacheEvict(value = "postsFeed", key = "#post.authorId")
    public Post createPost(Post post) {
        PostEntity entity = mapper.toEntity(post);

        mapAttachmentDetails(post, entity);

        PostEntity savedEntity = postJpaRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(
//            value = "postsFeed",
//            key = "#userId + ':' + #page",
//            condition = "#page == 0 && #size == 10"
//    )
    public List<PostSummary> getPosts(String userId, String filterAuthorId, String filterType, int page, int size) {
        var projections = postJpaRepository.findPostsWithStats(
                userId,
                filterAuthorId,
                filterType,
                PageRequest.of(page, size)
        );

        List<Long> postIds = projections.stream()
                .map(PostJpaRepository.PostSummaryProjection::getId)
                .toList();

        Map<Long, PostEntity> entityMap = postJpaRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(PostEntity::getId, Function.identity()));

        return projections.stream().map(proj -> {
            PostEntity entity = entityMap.get(proj.getId());

            Post.Attachment attachment = mapper.mapAttachmentToDomain(entity);

            return new PostSummary(
                    proj.getId(),
                    proj.getContent(),
                    attachment,
                    entity.getAttachmentType().toString(),
                    proj.getCreatedAt(),
                    new PostSummary.AuthorInfo(
                            proj.getAuthorId(),
                            proj.getAuthorFirstName(),
                            proj.getAuthorLastName(),
                            proj.getAuthorAvatarUrl()
                    ),
                    proj.getLikesCount(),
                    proj.getCommentsCount(),
                    proj.getRepostsCount(),
                    proj.getLikedByMe(),
                    proj.getRepostByMe(),
                    proj.getFavoritedByMe(),
                    proj.getUserVotedOptionId()
            );
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    //@Cacheable(value = "trendingTopics")
    public List<TrendingTopic> getTrendingTopics() {
        List<Object[]> results = postJpaRepository.findTrendingTagsRaw();
        return results.stream()
                .map(row -> new TrendingTopic(
                        "#" + (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void removePost(String userId, Long postId) {
        PostEntity postEntity = postJpaRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));

        if (!postEntity.getAuthorId().equals(userId)) {
            throw new RuntimeException("У вас нет прав на удаление этого поста");
        }

        postEntity.setDeleted(true);
        postJpaRepository.save(postEntity);
    }

    private String getPollOptionKey(Long postId, Long optionId) {
        return "poll:" + postId + ":option:" + optionId;
    }

    private String getPollVotersKey(Long postId) {
        return "poll:" + postId + ":voters";
    }

    @Override
    @Transactional
    public void voteInPoll(String userId, Long postId, Long optionId) {

        PollVoteId voteId = new PollVoteId(postId, userId);

        if (pollVoteJpaRepository.existsById(voteId)) {
            throw new RuntimeException("Вы уже голосовали в этом опросе");
        }
        System.out.println(optionId);
        PollVoteEntity voteEntity = new PollVoteEntity(postId, userId, optionId);
        pollVoteJpaRepository.save(voteEntity);

        redisTemplate.opsForValue().increment(getPollOptionKey(postId, optionId));

        redisTemplate.opsForSet().add(getPollVotersKey(postId), userId);

        // redisTemplate.opsForValue().increment("poll:" + postId + ":total_votes");
    }

    private void mapAttachmentDetails(Post post, PostEntity entity) {
        if (post.getAttachment() == null) return;

        switch (post.getAttachment()) {
            case Post.ImageAttachment img -> {
                var images = new ArrayList<PostAttachmentImageEntity>();
                int order = 0;
                for (String url : img.imageUrls()) {
                    var imageEntity = new PostAttachmentImageEntity();
                    imageEntity.setPost(entity);
                    imageEntity.setImage(url);
                    imageEntity.setOrder(order++);

                    imageEntity.setWidth(0);
                    imageEntity.setHeight(0);

                    images.add(imageEntity);
                }
                entity.setImageAttachments(images);
            }

            case Post.PollAttachment poll -> {
                var pollEntity = new PostAttachmentPollEntity();
                pollEntity.setPost(entity);
                pollEntity.setQuestion(poll.question());

                var options = new ArrayList<PostAttachmentPollOptionEntity>();
                if (poll.options() != null) {
                    for (var opt : poll.options()) {
                        var optEntity = new PostAttachmentPollOptionEntity();
                        optEntity.setPoll(pollEntity);
                        optEntity.setOption(opt.text());
                        options.add(optEntity);
                    }
                }
                pollEntity.setOptions(options);
                entity.setPollAttachment(pollEntity);
            }

            case Post.VideoAttachment video -> {
                var videoEntity = new PostAttachmentVideoEntity();
                videoEntity.setPost(entity);
                videoEntity.setVideo(video.videoUrl());
                videoEntity.setThumbnail(video.thumbnailUrl());
                //videoEntity.setDurationSeconds(0);

                entity.setVideoAttachment(videoEntity);
            }
        }
    }
}