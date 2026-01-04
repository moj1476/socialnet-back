package org.socialnet.socialnet.post.infrastructure.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.post.application.web.dto.NotificationResponse;
import org.socialnet.socialnet.post.core.port.output.LikeRepository;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.LikeEntity;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.LikeJpaRepository;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository.PostJpaRepository;
import org.socialnet.socialnet.user.core.port.output.ProfileRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final PostJpaRepository postJpaRepository;
    private final ProfileRepository profileRepository;

    private String getCountKey(Long postId) {
        return "post:" + postId + ":likes_count";
    }

    private String getUsersKey(Long postId) {
        return "post:" + postId + ":liked_users";
    }

    @Override
    @Transactional
    public void likePost(Long postId, String userId) {
        if (!likeJpaRepository.existsByUserIdAndPostId(userId, postId)) {

            likeJpaRepository.save(new LikeEntity(userId, postId));

            updateRedisOnLike(postId, userId);

            var post = postJpaRepository.findById(postId).get();
            var profile = profileRepository.findById(userId).get();

            messagingTemplate.convertAndSendToUser(
                    post.getAuthorId(),
                    "/queue/notifications",
                    new NotificationResponse(
                            "NEW_LIKE",
                            "Новый лайк к вашему посту",
                            "Пользователь " + profile.firstName() + " поставил вам лайк.",
                            null
                    ));
        }
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, String userId) {
        likeJpaRepository.deleteByUserIdAndPostId(userId, postId);

        updateRedisOnUnlike(postId, userId);
    }


    private void updateRedisOnLike(Long postId, String userId) {
        String countKey = getCountKey(postId);
        String usersKey = getUsersKey(postId);


        if (Boolean.TRUE.equals(redisTemplate.hasKey(countKey))) {
            redisTemplate.opsForValue().increment(countKey);
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey(usersKey))) {
            redisTemplate.opsForSet().add(usersKey, userId);
        }
    }

    private void updateRedisOnUnlike(Long postId, String userId) {
        String countKey = getCountKey(postId);
        String usersKey = getUsersKey(postId);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(countKey))) {
            redisTemplate.opsForValue().decrement(countKey);
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey(usersKey))) {
            redisTemplate.opsForSet().remove(usersKey, userId);
        }
    }

    public long getLikesCount(Long postId) {
        String key = getCountKey(postId);

        Integer cachedCount = (Integer) redisTemplate.opsForValue().get(key);
        if (cachedCount != null) {
            return cachedCount.longValue();
        }

        long dbCount = likeJpaRepository.countByPostId(postId);

        redisTemplate.opsForValue().set(key, dbCount, 1, TimeUnit.HOURS);

        return dbCount;
    }

    public boolean isLikedByUser(Long postId, String userId) {
        String key = getUsersKey(postId);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId));
        }

        boolean exists = likeJpaRepository.existsByUserIdAndPostId(userId, postId);

        return exists;
    }
}