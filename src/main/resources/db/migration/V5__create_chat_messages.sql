CREATE TABLE chat_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    session_id VARCHAR(120) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content LONGTEXT NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_chat_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_chat_session_created ON chat_messages (session_id, created_at);

