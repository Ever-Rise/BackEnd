package br.com.everrise.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record GerarTokenRequest(
        @NotNull Long pacienteId,
        @NotNull BigDecimal desconto,
        @NotNull Integer validadeDias
) {
}

