package org.socialnet.socialnet.post.application.configuration;

import org.socialnet.socialnet.post.core.port.input.RepostUseCase;
import org.socialnet.socialnet.post.core.port.output.RepostRepository;
import org.socialnet.socialnet.post.core.usecase.RepostUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepostUseCaseConfig {
    @Bean
    public RepostUseCase repostUseCase(RepostRepository repostRepository) {
        return new RepostUseCaseImpl(repostRepository);
    }
}
