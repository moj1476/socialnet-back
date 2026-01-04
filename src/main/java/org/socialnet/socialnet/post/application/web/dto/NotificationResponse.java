package org.socialnet.socialnet.post.application.web.dto;

public record NotificationResponse(
        String type,
        String title,
        String subtitle,
        Object metadata
) {
}
