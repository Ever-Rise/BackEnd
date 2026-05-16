CREATE TABLE chat_mensagem (
    id_mensagem     BIGINT NOT NULL AUTO_INCREMENT,
    id_chat_sessao  BIGINT NOT NULL,
    remetente       VARCHAR(50) NOT NULL,
    conteudo        TEXT NOT NULL,
    enviada_em      DATETIME(6) NOT NULL,
    PRIMARY KEY (id_mensagem),
    CONSTRAINT fk_mensagem_chat_sessao FOREIGN KEY (id_chat_sessao) REFERENCES chat_sessao (id_chat_sessao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

