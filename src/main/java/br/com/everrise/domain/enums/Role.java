package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    SUPER_ADMIN,
    ADMIN,
    OPERADOR,
    FAMILIA,
    VISITANTE;

    @JsonValue
    public String value() {
        return name();
    }
}

