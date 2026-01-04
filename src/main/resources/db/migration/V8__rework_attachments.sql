DROP TABLE IF EXISTS post_attachments_image;

CREATE TABLE post_attachments_image (
                                        id BIGSERIAL PRIMARY KEY,
                                        post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                                        image_url VARCHAR(512) NOT NULL,
                                        height INT,
                                        width INT,
                                        "order" INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_post_images_post_id ON post_attachments_image(post_id);