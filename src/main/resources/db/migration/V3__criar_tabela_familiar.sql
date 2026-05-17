CREATE TABLE familiar (
    id          BIGINT NOT NULL,
    parentesco  VARCHAR(100) NULL,
    id_paciente BIGINT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_familiar_usuario  FOREIGN KEY (id)          REFERENCES usuario  (id),
    CONSTRAINT fk_familiar_paciente FOREIGN KEY (id_paciente) REFERENCES paciente (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

