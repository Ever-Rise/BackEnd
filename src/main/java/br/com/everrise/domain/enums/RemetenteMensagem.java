package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RemetenteMensagem {
    PACIENTE,
    FAMILIAR,
    OPERADOR,
    IA;

    @JsonValue
    public String value() {
        return name();
    }
}

