package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusEquipamento {
    DESLIGADO,
    PRONTO,
    EM_CONTROLE,
    EM_SESSAO,
    PAUSADO,
    SAFETY_HOLD,
    ERRO,
    EMERGENCIA;

    @JsonValue
    public String value() {
        return name();
    }
}

