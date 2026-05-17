CREATE TABLE equipamento (
    id_equipamento      BIGINT NOT NULL AUTO_INCREMENT,
    identificador       VARCHAR(100) NOT NULL,
    descricao           VARCHAR(255) NULL,
    status              VARCHAR(50) NOT NULL,
    localizacao         VARCHAR(255) NULL,
    bateria             INT NULL,
    ultima_atualizacao  DATETIME(6) NULL,
    PRIMARY KEY (id_equipamento),
    UNIQUE KEY uk_equipamento_identificador (identificador)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

