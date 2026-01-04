package org.socialnet.socialnet.message.application.web.controller;

import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.message.application.web.dto.CallSignal;
import org.socialnet.socialnet.message.application.web.dto.CallSignalResponse;
import org.socialnet.socialnet.message.core.model.Call;
import org.socialnet.socialnet.message.core.port.input.ConversationUseCase;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class SignalingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationUseCase conversationUseCase;

    @MessageMapping("/call/start")
    public void handleSignal(@Payload CallSignal signal, Principal principal) {

        Call call = conversationUseCase.getConversationParticipantsToCall(
                signal.chatId(),
                principal.getName()
        );

        for (Call.Participant participant : call.participants()) {
            if (!participant.userId().equals(principal.getName())) {
                messagingTemplate.convertAndSendToUser(
                        participant.userId(),
                        "/queue/notifications",
                        new CallSignalResponse(
                                "INCOMING_CALL", //TODO: это все уведы, нужно будет вынести в enum
                                call.title(),
                                call.callerName(),
                                call.callerId(),
                                call.callerAvatarUrl(),
                                signal.chatId().toString()
                ));
            }
        }
    }
}