package org.socialnet.socialnet.message.application.configuration;

import org.socialnet.socialnet.message.core.port.input.ConversationUseCase;
import org.socialnet.socialnet.message.core.usecase.ConversationUseCaseImpl;
import org.socialnet.socialnet.message.infrastructure.persistence.jpa.adapter.ConversationRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConversationUseCaseConfig {

    @Bean
    public ConversationUseCase conversationUseCase(ConversationRepositoryImpl repository) {
        return new ConversationUseCaseImpl(repository);
    }
}
