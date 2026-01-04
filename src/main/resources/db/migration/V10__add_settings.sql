CREATE TYPE visibility_type AS ENUM ('EVERYONE', 'FRIENDS', 'NOBODY');
CREATE TYPE app_theme AS ENUM ('LIGHT', 'DARK', 'SYSTEM');

CREATE TABLE user_settings (
                               user_id varchar(50) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,

                               like_notification BOOLEAN NOT NULL DEFAULT TRUE,
                               comment_notification BOOLEAN NOT NULL DEFAULT TRUE,
                               friend_notification BOOLEAN NOT NULL DEFAULT TRUE,
                               message_notification BOOLEAN NOT NULL DEFAULT TRUE,

                               who_can_see_posts visibility_type NOT NULL DEFAULT 'EVERYONE',
                               show_online_status BOOLEAN NOT NULL DEFAULT TRUE,

                               theme app_theme NOT NULL DEFAULT 'SYSTEM',

                               updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);