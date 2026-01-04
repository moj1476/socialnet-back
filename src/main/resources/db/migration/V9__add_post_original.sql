CREATE TABLE reposts (
                         id BIGSERIAL PRIMARY KEY,
                         user_id varchar(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                         is_private BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                         UNIQUE(user_id, post_id, is_private)
);

CREATE INDEX idx_reposts_user_id ON reposts(user_id);
CREATE INDEX idx_reposts_post_id ON reposts(post_id);