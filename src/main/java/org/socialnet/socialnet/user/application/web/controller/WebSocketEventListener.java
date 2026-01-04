package org.socialnet.socialnet.user.application.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    private static final String ONLINE_USERS_KEY = "user:online:";

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        if (user != null) {
            String userId = user.getName();
            log.info("User connected: {}", userId);

            redisTemplate.opsForValue().set(ONLINE_USERS_KEY + userId, "true", 1, TimeUnit.DAYS);

            notifyUserStatusChange(userId, true);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();

        if (user != null) {
            String userId = user.getName();
            log.info("User disconnected: {}", userId);

            redisTemplate.delete(ONLINE_USERS_KEY + userId);

            notifyUserStatusChange(userId, false);
        }
    }
    
    private void notifyUserStatusChange(String userId, boolean isOnline) {
        var statusUpdate = Map.of(
            "userId", userId,
            "isOnline", isOnline
        );
        
        messagingTemplate.convertAndSend("/topic/user.status", statusUpdate);
    }
}