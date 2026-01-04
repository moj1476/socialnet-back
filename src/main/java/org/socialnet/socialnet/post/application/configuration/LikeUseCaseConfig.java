package org.socialnet.socialnet.post.application.configuration;

import org.socialnet.socialnet.post.core.port.input.LikeUseCase;
import org.socialnet.socialnet.post.core.port.output.LikeRepository;
import org.socialnet.socialnet.post.core.usecase.LikeUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LikeUseCaseConfig {
    @Bean
    public LikeUseCase createLikeUseCase(LikeRepository likeRepository) {
        return new LikeUseCaseImpl(likeRepository);
    }
}
