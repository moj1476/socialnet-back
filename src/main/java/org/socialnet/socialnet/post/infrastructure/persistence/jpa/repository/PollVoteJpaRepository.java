package org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository;

import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.PollVoteEntity;
import org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity.PollVoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollVoteJpaRepository extends JpaRepository<PollVoteEntity, PollVoteId> {
    // existsById
}