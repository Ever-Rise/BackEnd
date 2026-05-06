package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoAlerta {
    OBSTACULO("Obstáculo Detectado"),
    BATERIA_BAIXA("Bateria Baixa"),
    CONEXAO_FRACA("Conexão Fraca"),
    ANOMALIA("Anomalia Detectada"),
    SOBRECARGA("Sobrecarga Detectada"),
    DESCONEXAO("Desconexão"),
    TIMEOUT("Timeout de Sessão");

    private final String descricao;

    TipoAlerta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @JsonValue
    public String value() {
        return name();
    }
}
