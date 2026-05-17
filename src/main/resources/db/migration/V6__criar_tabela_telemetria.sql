CREATE TABLE telemetria (
    id_telemetria   BIGINT NOT NULL AUTO_INCREMENT,
    id_equipamento  BIGINT NOT NULL,
    id_sessao       BIGINT NULL,
    timestamp       DATETIME(6) NOT NULL,
    payload         TEXT NULL,
    PRIMARY KEY (id_telemetria),
    CONSTRAINT fk_telemetria_equipamento FOREIGN KEY (id_equipamento) REFERENCES equipamento (id_equipamento),
    CONSTRAINT fk_telemetria_sessao      FOREIGN KEY (id_sessao)      REFERENCES sessao_uso  (id_sessao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

