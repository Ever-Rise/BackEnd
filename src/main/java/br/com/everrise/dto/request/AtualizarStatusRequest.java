package br.com.everrise.dto.request;

import br.com.everrise.domain.enums.StatusEquipamento;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(@NotNull StatusEquipamento status) {
}

