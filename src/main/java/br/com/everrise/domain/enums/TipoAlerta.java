package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TipoAlerta {
    SOBRECARGA("Sobrecarga"),
    OBSTACULO("Obstáculo"),
    CONEXAO("Conexão"),
    BATERIA("Bateria");

    public static final TipoAlerta BATERIA_BAIXA = BATERIA;

    public static final TipoAlerta CONEXAO_FRACA = CONEXAO;

    public static final TipoAlerta ANOMALIA = SOBRECARGA;

    public static final TipoAlerta DESCONEXAO = CONEXAO;

    public static final TipoAlerta TIMEOUT = CONEXAO;

    private final String descricao;

    TipoAlerta(String descricao) {
        this.descricao = descricao;
    }


    @JsonValue
    public String value() {
        return name();
    }
}
