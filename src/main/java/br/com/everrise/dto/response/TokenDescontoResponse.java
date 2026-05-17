package br.com.everrise.dto.response;

import br.com.everrise.domain.TokenDesconto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TokenDescontoResponse(
        Long id,
        String codigo,
        BigDecimal desconto,
        Long pacienteId,
        LocalDateTime emitidoEm,
        LocalDateTime expiraEm,
        Boolean utilizado
) {
    public static TokenDescontoResponse from(TokenDesconto t) {
        return new TokenDescontoResponse(
                t.getId(),
                t.getCodigo(),
                t.getDesconto(),
                t.getPaciente() == null ? null : t.getPaciente().getId(),
                t.getEmitidoEm(),
                t.getExpiraEm(),
                t.getUtilizado()
        );
    }
}

