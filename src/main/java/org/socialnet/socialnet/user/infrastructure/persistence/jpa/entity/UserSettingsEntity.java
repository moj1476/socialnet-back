package org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
public class UserSettingsEntity {

    @Id
    @Column(name = "user_id")
    private String userId;


    @Column(name = "like_notification", nullable = false)
    private Boolean likeNotification = true;

    @Column(name = "comment_notification", nullable = false)
    private Boolean commentNotification = true;

    @Column(name = "friend_notification", nullable = false)
    private Boolean friendNotification = true;

    @Column(name = "message_notification", nullable = false)
    private Boolean messageNotification = true;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "who_can_see_posts", nullable = false, columnDefinition = "visibility_type")
    private VisibilityType whoCanSeePosts = VisibilityType.EVERYONE;

    @Column(name = "show_online_status", nullable = false)
    private Boolean showOnlineStatus = true;


    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "theme", nullable = false, columnDefinition = "app_theme")
    private AppTheme theme = AppTheme.SYSTEM;


    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public enum VisibilityType {
        EVERYONE,
        FRIENDS,
        NOBODY
    }

    public enum AppTheme {
        LIGHT,
        DARK,
        SYSTEM
    }
}