CREATE TABLE usuario (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    tipo_usuario     VARCHAR(31) NOT NULL,
    nome             VARCHAR(150) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    senha_hash       VARCHAR(255) NOT NULL,
    role             VARCHAR(50) NOT NULL,
    criado_em        DATETIME(6) NOT NULL,
    ativo            TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

