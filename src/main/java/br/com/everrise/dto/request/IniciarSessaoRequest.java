package br.com.everrise.dto.request;

import jakarta.validation.constraints.NotNull;

public record IniciarSessaoRequest(
        @NotNull Long equipamentoId,
        @NotNull Long operadorId,
        Long pacienteId
) {
}

