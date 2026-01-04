package org.socialnet.socialnet.message.application.configuration;

import org.socialnet.socialnet.message.core.port.input.MessageUseCase;
import org.socialnet.socialnet.message.core.port.input.SendMessageUseCase;
import org.socialnet.socialnet.message.core.port.output.MessageRepository;
import org.socialnet.socialnet.message.core.usecase.MessageUseCaseImpl;
import org.socialnet.socialnet.message.core.usecase.SendMessageUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageUseCaseConfig {

    @Bean
    public SendMessageUseCase sendMessageUseCase(MessageRepository messageRepository) {
        return new SendMessageUseCaseImpl(messageRepository);
    }

    @Bean
    public MessageUseCase messageUseCase(MessageRepository messageRepository) {
        return new MessageUseCaseImpl(messageRepository);
    }
}