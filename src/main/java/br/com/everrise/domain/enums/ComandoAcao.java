package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ComandoAcao {
    FRENTE,
    TRAS,
    ESQUERDA,
    DIREITA,
    SUBIR,
    DESCER,
    PARAR,
    EMERGENCIA_STOP;

    @JsonValue
    public String value() {
        return name();
    }

    public boolean isEmergency() {
        return this == EMERGENCIA_STOP;
    }
}

