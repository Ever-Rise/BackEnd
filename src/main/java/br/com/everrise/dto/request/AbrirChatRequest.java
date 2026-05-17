package br.com.everrise.dto.request;

import jakarta.validation.constraints.NotNull;

public record AbrirChatRequest(
        @NotNull Long pacienteId,
        Long usuarioId
) {
}

