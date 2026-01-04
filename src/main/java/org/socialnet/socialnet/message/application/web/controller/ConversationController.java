package org.socialnet.socialnet.message.application.web.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.socialnet.socialnet.message.application.web.dto.*;
import org.socialnet.socialnet.message.application.web.mapper.ConversationApiMapper;
import org.socialnet.socialnet.message.application.web.mapper.MessageApiMapper;
import org.socialnet.socialnet.message.core.port.input.ConversationUseCase;
import org.socialnet.socialnet.message.core.port.input.MessageUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversation")
@AllArgsConstructor
public class ConversationController {

    private final ConversationUseCase conversationRepository;
    private final ConversationApiMapper conversationApiMapper;
    private final MessageUseCase messageUseCase;
    private final MessageApiMapper messageApiMapper;

    @GetMapping("/all")
    public ResponseEntity<List<ConversationDto>> getConversations(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                conversationRepository.getConversations(jwt.getSubject()).stream()
                        .map(conversation -> new ConversationDto(
                                conversation.getId(),
                                conversation.getTitle(),
                                conversation.getAvatarUrl(),
                                conversation.getLastMessage(),
                                conversation.getIsOnline()
                        ))
                        .toList()
        );
    }

    @DeleteMapping("/current/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long conversationId) {

        conversationRepository.deleteConversation(conversationId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current/{conversationId}")
    public ResponseEntity<DetailedConversationDto> getConversation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String conversationId) {
        var conversation = conversationRepository.getConversationByIdAndUserId(conversationId, jwt.getSubject());
        return ResponseEntity.ok(
                conversationApiMapper.toDetailedDto(conversation.get())
        );
    }

    @PostMapping("/create")
    public ResponseEntity<?> createConversation(@AuthenticationPrincipal Jwt jwt,
                                                @RequestBody @Valid CreateConversationDto createConversationDto) {

        conversationRepository.createConversation(
                createConversationDto.participantIds(),
                createConversationDto.title(),
                createConversationDto.message(),
                jwt.getSubject()
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current/{conversationId}/messages")
    public ResponseEntity<MessageResponse> getMessagesForConversation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String conversationId,
            @RequestParam int page,
            @RequestParam int size) {
        var response = messageUseCase.getMessagesForConversation(conversationId, jwt.getSubject(), page, size);
        var messageDtos = response.messages().stream()
                .map(messageApiMapper::toDto)
                .toList();
        var messageResponse = messageApiMapper.toMessageResponse(
                messageDtos,
                response.totalCount()
        );
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/current/{conversationId}/messages/search")
    public ResponseEntity<List<MessageDto>> searchMessages(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long conversationId,
            @RequestParam String query) {

        var foundMessages = messageUseCase.searchMessages(
                Long.valueOf(conversationId),
                jwt.getSubject(),
                query
        );

        return ResponseEntity.ok(
                foundMessages.stream()
                        .map(messageApiMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/current/{conversationId}/messages/around/{messageId}")
    public ResponseEntity<MessageResponse> getMessagesAround(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long conversationId,
            @PathVariable Long messageId) {

        var messages = messageUseCase.findMessagesAround(
                conversationId,
                jwt.getSubject(),
                messageId,
                30
        );

        var messageDtos = messages.stream()
                .map(messageApiMapper::toDto)
                .toList();

        return ResponseEntity.ok(messageApiMapper.toMessageResponse(messageDtos, messages.size()));
    }

    @GetMapping("/current/{conversationId}/messages/load")
    public ResponseEntity<List<MessageDto>> loadMessagesByCursor(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long conversationId,
            @RequestParam Long messageId,
            @RequestParam String direction, //TODO: enum
            @RequestParam(defaultValue = "20") int limit) {

        var messages = messageUseCase.loadMessagesRelative(
                conversationId,
                jwt.getSubject(),
                messageId,
                direction,
                limit
        );

        return ResponseEntity.ok(
                messages.stream()
                        .map(messageApiMapper::toDto)
                        .toList()
        );
    }

}