package org.socialnet.socialnet.post.application.configuration;

import org.socialnet.socialnet.post.core.port.input.PostUseCase;
import org.socialnet.socialnet.post.core.port.output.PostRepository;
import org.socialnet.socialnet.post.core.usecase.PostUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public PostUseCase createPostUseCase(PostRepository postRepository) {
        return new PostUseCaseImpl(postRepository);
    }

}
