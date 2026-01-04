package org.socialnet.socialnet.post.application.configuration;

import org.socialnet.socialnet.message.core.port.input.MessageUseCase;
import org.socialnet.socialnet.message.core.port.output.MessageRepository;
import org.socialnet.socialnet.message.core.usecase.MessageUseCaseImpl;
import org.socialnet.socialnet.post.core.port.input.CommentUseCase;
import org.socialnet.socialnet.post.core.port.output.CommentRepository;
import org.socialnet.socialnet.post.core.usecase.CommentUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommentUseCaseConfig {
    @Bean
    public CommentUseCase commentUseCase(CommentRepository repository) {
        return new CommentUseCaseImpl(repository);
    }
}
