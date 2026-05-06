package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoPlano {
    FAMILIA("Plano Familia"),
    CLINICA("Plano Clinica");

    private final String displayName;

    TipoPlano(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonValue
    public String value() {
        return name();
    }
}
