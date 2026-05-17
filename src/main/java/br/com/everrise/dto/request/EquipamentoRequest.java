package br.com.everrise.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EquipamentoRequest(
        @NotBlank String identificador,
        String descricao,
        String localizacao
) {
}

