package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusSessao {
    ATIVA,
    ENCERRADA,
    INTERRUPTED;

    @JsonValue
    public String value() {
        return name();
    }
}

