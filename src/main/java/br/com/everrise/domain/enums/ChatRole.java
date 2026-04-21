package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatRole {
    USER,
    BOT;

    @JsonValue
    public String value() {
        return name();
    }
}
