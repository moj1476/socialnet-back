CREATE TYPE user_role AS ENUM ('USER', 'MODER', 'ADMIN');
CREATE TYPE friendship_status AS ENUM ('PENDING', 'ACCEPTED', 'DECLINED', 'BLOCKED');
CREATE TYPE message_type AS ENUM ('TEXT', 'IMAGE', 'FILE', 'SYSTEM');
CREATE TYPE conversation_type AS ENUM ('PRIVATE', 'GROUP');
CREATE TYPE notification_type AS ENUM ('LIKE', 'COMMENT', 'FRIEND_REQUEST', 'FRIEND_ACCEPTED', 'MENTION', 'POST_SHARED');
CREATE TYPE notification_entity_type AS ENUM ('POST', 'USER', 'COMMENT', 'MESSAGE');
CREATE TYPE post_attachment_type AS ENUM ('IMAGE', 'VIDEO', 'POLL');

CREATE TABLE users (
    id varchar(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'USER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);


CREATE TABLE profiles (
    id varchar(50) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    avatar_url VARCHAR(512),
    bio TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE posts (
   id BIGSERIAL PRIMARY KEY,
   author_id varchar(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
   content TEXT,
   attachment_type post_attachment_type,
   is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
   deleted_at TIMESTAMPTZ,
   deleted_by varchar(50) REFERENCES users(id) ON DELETE SET NULL,
   created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_posts_author_id ON posts(author_id);

CREATE TABLE post_attachments_image (
   post_id BIGINT PRIMARY KEY REFERENCES posts(id) ON DELETE CASCADE,
   image_url VARCHAR(512) NOT NULL,
   height INT,
   width INT
);
CREATE TABLE post_attachments_video (
      post_id BIGINT PRIMARY KEY REFERENCES posts(id) ON DELETE CASCADE,
      video_url VARCHAR(512) NOT NULL,
      thumbnail_url VARCHAR(512),
      duration_seconds INT
);

CREATE TABLE post_attachments_poll (
    post_id BIGINT PRIMARY KEY REFERENCES posts(id) ON DELETE CASCADE,
    question TEXT NOT NULL,
    ends_at TIMESTAMPTZ
);

CREATE TABLE post_attachments_poll_option (
     id BIGSERIAL PRIMARY KEY,
     poll_post_id BIGINT NOT NULL REFERENCES post_attachments_poll(post_id) ON DELETE CASCADE,
     option_text VARCHAR(255) NOT NULL
);
CREATE INDEX idx_poll_options_post_id ON post_attachments_poll_option(poll_post_id);

CREATE TABLE post_attachments_poll_vote (
  poll_post_id BIGINT NOT NULL REFERENCES post_attachments_poll(post_id) ON DELETE CASCADE,
  user_id varchar(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  option_id BIGINT NOT NULL REFERENCES post_attachments_poll_option(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (poll_post_id, user_id)
);




CREATE TABLE comments (
     id BIGSERIAL PRIMARY KEY,
     author_id varchar(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
     post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
     parent_comment_id BIGINT REFERENCES comments(id) ON DELETE CASCADE,
     content TEXT NOT NULL,
     is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_author_id ON comments(author_id);
CREATE INDEX idx_comments_parent_comment_id ON comments(parent_comment_id);


CREATE TABLE friendships (
   id BIGSERIAL PRIMARY KEY,
   requester_id varchar(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
   addressee_id varchar(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
   status friendship_status NOT NULL DEFAULT 'PENDING',
   created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   UNIQUE (requester_id, addressee_id),
   CHECK (requester_id <> addressee_id)
);
CREATE INDEX idx_friendships_requester_id ON friendships(requester_id);
CREATE INDEX idx_friendships_addressee_id ON friendships(addressee_id);


CREATE TABLE conversations (
  id BIGSERIAL PRIMARY KEY,
  type conversation_type NOT NULL,
  title VARCHAR(255),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE conversation_participants (
     id BIGSERIAL PRIMARY KEY,
     conversation_id BIGINT NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
     user_id varchar(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
     UNIQUE (conversation_id, user_id)
);
CREATE INDEX idx_conversation_participants_user_id ON conversation_participants(user_id);


CREATE TABLE messages (
     id BIGSERIAL PRIMARY KEY,
     conversation_id BIGINT NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
     sender_id varchar(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
     content TEXT,
     type message_type NOT NULL DEFAULT 'TEXT',
     is_read BOOLEAN NOT NULL DEFAULT FALSE,
     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);


CREATE TABLE likes (
   id BIGSERIAL PRIMARY KEY,
   user_id varchar(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
   post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE,
   comment_id BIGINT REFERENCES comments(id) ON DELETE CASCADE,
   created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   UNIQUE (user_id, post_id),
   UNIQUE (user_id, comment_id),
   CHECK ( (post_id IS NOT NULL AND comment_id IS NULL) OR (post_id IS NULL AND comment_id IS NOT NULL) )
);
CREATE INDEX idx_likes_post_id ON likes(post_id);
CREATE INDEX idx_likes_comment_id ON likes(comment_id);

CREATE TABLE notifications (
       id BIGSERIAL PRIMARY KEY,
       recipient_id varchar(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
       actor_id varchar(50) REFERENCES users(id) ON DELETE SET NULL,
       type notification_type NOT NULL,
       entity_type notification_entity_type,
       entity_id BIGINT,
       message VARCHAR(512),
       is_read BOOLEAN NOT NULL DEFAULT FALSE,
       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_notifications_recipient_id ON notifications(recipient_id);
CREATE INDEX idx_notifications_entity ON notifications(entity_type, entity_id);