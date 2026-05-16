package br.com.everrise.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReconhecerAlertaRequest(@NotNull Long usuarioId) {
}

