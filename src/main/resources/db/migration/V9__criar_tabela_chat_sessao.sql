CREATE TABLE chat_sessao (
    id_chat_sessao  BIGINT NOT NULL AUTO_INCREMENT,
    id_paciente     BIGINT NOT NULL,
    id_usuario      BIGINT NULL,
    criada_em       DATETIME(6) NOT NULL,
    atualizada_em   DATETIME(6) NULL,
    PRIMARY KEY (id_chat_sessao),
    CONSTRAINT fk_chat_sessao_paciente FOREIGN KEY (id_paciente) REFERENCES paciente (id),
    CONSTRAINT fk_chat_sessao_usuario  FOREIGN KEY (id_usuario)  REFERENCES usuario  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

