package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PedidoStatus {
    PENDENTE,
    PAGO,
    CANCELADO,
    REEMBOLSADO;

    @JsonValue
    public String value() {
        return name();
    }
}
