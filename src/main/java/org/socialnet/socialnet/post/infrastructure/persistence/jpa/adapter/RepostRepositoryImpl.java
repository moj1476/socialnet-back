package org.socialnet.socialnet.post.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.post.core.model.Post;
import org.socialnet.socialnet.post.core.model.PostSummary;
import org.socialnet.socialnet.post.core.port.output.RepostRepository;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.PostEntity;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.RepostEntity;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.mapper.PostPersistenceMapper;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.PostJpaRepository;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.RepostJpaRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RepostRepositoryImpl implements RepostRepository {

    private final RepostJpaRepository repostJpaRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostPersistenceMapper mapper;
    private final PostJpaRepository postJpaRepository;

    private String getCountKey(Long postId) {
        return "post:" + postId + ":reposts_count";
    }

    @Override
    @Transactional
    public void createRepost(String userId, Long postId, boolean isPrivate) {
        if (!repostJpaRepository.existsByUserIdAndPostIdAndIsPrivate(userId, postId, isPrivate)) {
            repostJpaRepository.save(new RepostEntity(userId, postId, isPrivate));

            String key = getCountKey(postId);
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.opsForValue().increment(key);
            }
        }
    }

    @Override
    @Transactional
    public void deleteRepost(String userId, Long postId, boolean isPrivate) {
        repostJpaRepository.deleteByUserIdAndPostIdAndIsPrivate(userId, postId, isPrivate);

        String key = getCountKey(postId);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().decrement(key);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getRepostsCount(Long postId) {
        String key = getCountKey(postId);

        Integer cached = (Integer) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached.longValue();
        }

        long count = repostJpaRepository.countByPostId(postId);

        redisTemplate.opsForValue().set(key, count, 2, TimeUnit.HOURS);

        return count;
    }

    @Override
    public List<PostSummary> getReposts(String userId, Boolean isPrivate) {
        List<PostJpaRepository.PostSummaryProjection> projections;
        if (isPrivate == null) {
            projections = repostJpaRepository.findRepostsByUserId(userId);
        } else {
            projections = repostJpaRepository.findRepostsByUserIdAndIsPrivate(userId, isPrivate);
        }

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
}