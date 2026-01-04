package org.socialnet.socialnet.user.core.model;

public record SettingsEntity(
        String userId,
        Boolean likeNotification,
        Boolean commentNotification,
        Boolean friendNotification,
        Boolean messageNotification,
        VisibilityType whoCanSeePosts,
        Boolean showOnlineStatus,
        Theme theme
) {
}
