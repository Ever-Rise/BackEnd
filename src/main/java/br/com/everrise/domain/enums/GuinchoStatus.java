package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GuinchoStatus {
    DESLIGADO("Desligado"),
    PRONTO("Pronto para Usar"),
    EM_MOVIMENTO("Em Movimento"),
    PAUSADO("Pausado"),
    SAFETY_HOLD("Parada de Segurança - Obstáculo Detectado"),
    ERRO("Em Erro"),
    EMERGENCIA("Emergência Ativada - Reset Manual Requerido");

    private final String descricao;

    GuinchoStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @JsonValue
    public String value() {
        return name();
    }

    public boolean isOperational() {
        return this != ERRO && this != EMERGENCIA && this != SAFETY_HOLD;
    }

    public boolean isSafeToMove() {
        return this == PRONTO;
    }

    public boolean isEmergencyOrSafetyHold() {
        return this == EMERGENCIA || this == SAFETY_HOLD;
    }

    public boolean canAcceptCommands() {
        return !isEmergencyOrSafetyHold() && this != DESLIGADO && this != ERRO;
    }
}
