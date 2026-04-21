package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GuinchoStatus {
    DESLIGADO,
    PRONTO,
    EM_MOVIMENTO,
    PAUSADO,
    ERRO,
    EMERGENCIA;

    @JsonValue
    public String value() {
        return name();
    }

    public boolean isOperational() {
        return this != ERRO && this != EMERGENCIA;
    }

    public boolean isSafeToMove() {
        return this == PRONTO;
    }
}
