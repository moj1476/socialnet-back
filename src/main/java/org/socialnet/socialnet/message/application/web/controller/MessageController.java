package org.socialnet.socialnet.message.application.web.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.socialnet.socialnet.message.application.web.dto.SendMessageDto;
import org.socialnet.socialnet.message.application.web.mapper.MessageApiMapper;
import org.socialnet.socialnet.message.core.port.input.ConversationUseCase;
import org.socialnet.socialnet.message.core.port.input.SendMessageUseCase;
import org.socialnet.socialnet.post.application.web.dto.NotificationResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class MessageController {

    private final SendMessageUseCase sendMessageUseCase;
    private final MessageApiMapper messageApiMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationUseCase conversationUseCase;

    @MessageMapping("/conversations/{conversationId}/send")
    public void sendMessage(
            @DestinationVariable Long conversationId,
            @Payload @Valid SendMessageDto dto,
            Principal principal
    ) {
        String senderId = principal.getName();

        var command = messageApiMapper.toCommand(dto, senderId, conversationId);

        var savedMessage = sendMessageUseCase.handle(command);

        var messageDto = messageApiMapper.toDto(savedMessage);

        String destination = "/topic/conversations/" + conversationId;

        messagingTemplate.convertAndSend(destination, messageDto);

        List<String> recipientIds = conversationUseCase.getConversationParticipants(conversationId);

        for (String recipientId : recipientIds) {
            if (recipientId.equals(senderId)) {
                continue;
            }
            log.info("Sending message to conversation {}", recipientId);
            messagingTemplate.convertAndSendToUser(recipientId, "/queue/messages", messageDto);

            messagingTemplate.convertAndSendToUser(
                    recipientId,
                    "/queue/notifications",
                    new NotificationResponse(
                            "NEW_MESSAGE",
                            null,
                            "У вас новое сообщение в разговоре.",
                            null
                    ));
        }
    }

}