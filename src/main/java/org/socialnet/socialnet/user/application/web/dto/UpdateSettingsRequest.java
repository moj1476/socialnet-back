package org.socialnet.socialnet.user.application.web.dto;

import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserSettingsEntity.VisibilityType;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserSettingsEntity.AppTheme;

public record UpdateSettingsRequest(
        Boolean likeNotification,
        Boolean commentNotification,
        Boolean friendNotification,
        Boolean messageNotification,
        VisibilityType whoCanSeePosts,
        Boolean showOnlineStatus,
        AppTheme theme
) {}