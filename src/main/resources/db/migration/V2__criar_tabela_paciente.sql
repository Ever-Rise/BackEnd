CREATE TABLE paciente (
    id                BIGINT NOT NULL,
    data_nascimento   DATETIME(6) NULL,
    condicao_medica   VARCHAR(500) NULL,
    observacoes       TEXT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_paciente_usuario FOREIGN KEY (id) REFERENCES usuario (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

