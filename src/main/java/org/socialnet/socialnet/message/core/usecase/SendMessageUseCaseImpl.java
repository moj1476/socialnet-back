package org.socialnet.socialnet.message.core.usecase;

import org.socialnet.socialnet.message.core.model.Message;
import org.socialnet.socialnet.message.core.port.input.SendMessageUseCase;
import org.socialnet.socialnet.message.core.port.output.MessageRepository;

import java.util.Objects;

public class SendMessageUseCaseImpl implements SendMessageUseCase {

    private final MessageRepository messageRepository;

    public SendMessageUseCaseImpl(MessageRepository messageRepository) {
        this.messageRepository = Objects.requireNonNull(messageRepository);
    }

    @Override
    public Message handle(SendMessageCommand command) {

        Message newMessage = Message.createNew(
                command.conversationId(),
                command.senderId(),
                command.content(),
                command.type()
        );

        return messageRepository.save(newMessage);
    }
}