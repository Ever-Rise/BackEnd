CREATE TABLE alerta (
    id_alerta       BIGINT NOT NULL AUTO_INCREMENT,
    tipo            VARCHAR(50) NOT NULL,
    descricao       VARCHAR(255) NULL,
    gerado_em       DATETIME(6) NOT NULL,
    reconhecido     TINYINT(1) NOT NULL DEFAULT 0,
    reconhecido_em  DATETIME(6) NULL,
    reconhecido_por BIGINT NULL,
    id_equipamento  BIGINT NOT NULL,
    id_sessao       BIGINT NULL,
    PRIMARY KEY (id_alerta),
    CONSTRAINT fk_alerta_equipamento FOREIGN KEY (id_equipamento) REFERENCES equipamento (id_equipamento),
    CONSTRAINT fk_alerta_sessao      FOREIGN KEY (id_sessao)      REFERENCES sessao_uso  (id_sessao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

