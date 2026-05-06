CREATE TABLE guinchos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    apelido VARCHAR(120),
    status VARCHAR(30) NOT NULL,
    battery INT,
    connection_quality INT,
    is_moving BOOLEAN NOT NULL,
    last_seen DATETIME,
    owner_id BIGINT,
    CONSTRAINT fk_guincho_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE guincho_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    guincho_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    device_id VARCHAR(100) NOT NULL,
    started_at DATETIME NOT NULL,
    ended_at DATETIME,
    active BOOLEAN NOT NULL,
    CONSTRAINT fk_session_guincho FOREIGN KEY (guincho_id) REFERENCES guinchos(id),
    CONSTRAINT fk_session_user FOREIGN KEY (user_id) REFERENCES users(id)
);

