CREATE TABLE sessao_uso (
    id_sessao       BIGINT NOT NULL AUTO_INCREMENT,
    id_equipamento  BIGINT NOT NULL,
    id_operador     BIGINT NOT NULL,
    id_paciente     BIGINT NULL,
    status          VARCHAR(50) NOT NULL,
    iniciada_em     DATETIME(6) NOT NULL,
    encerrada_em    DATETIME(6) NULL,
    PRIMARY KEY (id_sessao),
    CONSTRAINT fk_sessao_equipamento FOREIGN KEY (id_equipamento) REFERENCES equipamento (id_equipamento),
    CONSTRAINT fk_sessao_operador    FOREIGN KEY (id_operador)    REFERENCES usuario     (id),
    CONSTRAINT fk_sessao_paciente    FOREIGN KEY (id_paciente)    REFERENCES paciente    (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

