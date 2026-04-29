CREATE TABLE alertas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    guincho_id BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    descricao VARCHAR(255),
    reconhecido BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em DATETIME NOT NULL,
    reconhecido_em DATETIME,
    reconhecido_por BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_alerta_guincho FOREIGN KEY (guincho_id) REFERENCES guinchos(id),
    CONSTRAINT fk_alerta_usuario FOREIGN KEY (reconhecido_por) REFERENCES users(id),
    INDEX idx_alerta_guincho_reconhecido (guincho_id, reconhecido),
    INDEX idx_alerta_criado_em (criado_em)
);
