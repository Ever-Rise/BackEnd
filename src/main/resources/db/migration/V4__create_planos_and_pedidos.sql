CREATE TABLE planos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo VARCHAR(30) NOT NULL UNIQUE,
    preco DECIMAL(10,2) NOT NULL,
    max_dispositivos INT NOT NULL,
    descricao VARCHAR(255) NOT NULL
);

CREATE TABLE pedidos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plano_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    mercado_pago_payment_id VARCHAR(120),
    cupom_desconto VARCHAR(80),
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_pedido_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_pedido_plano FOREIGN KEY (plano_id) REFERENCES planos(id)
);

