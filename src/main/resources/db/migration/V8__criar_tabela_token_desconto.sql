CREATE TABLE token_desconto (
	id_token    BIGINT NOT NULL AUTO_INCREMENT,
	codigo      VARCHAR(100) NOT NULL,
	desconto    DECIMAL(10,2) NOT NULL,
	id_paciente BIGINT NOT NULL,
	emitido_em  DATETIME(6) NOT NULL,
	expira_em   DATETIME(6) NOT NULL,
	utilizado   TINYINT(1) NOT NULL DEFAULT 0,
	PRIMARY KEY (id_token),
	UNIQUE KEY uk_token_codigo (codigo),
	CONSTRAINT fk_token_paciente FOREIGN KEY (id_paciente) REFERENCES paciente (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

