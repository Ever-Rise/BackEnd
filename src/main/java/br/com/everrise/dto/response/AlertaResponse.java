package br.com.everrise.dto.response;

import br.com.everrise.domain.Alerta;

import java.time.LocalDateTime;

public record AlertaResponse(
        Long id,
        String tipo,
        String descricao,
        LocalDateTime geradoEm,
        Boolean reconhecido,
        LocalDateTime reconhecidoEm,
        Long reconhecidoPor,
        Long equipamentoId
) {
    public static AlertaResponse from(Alerta a) {
        return new AlertaResponse(
                a.getId(),
                a.getTipo() == null ? null : a.getTipo().name(),
                a.getDescricao(),
                a.getGeradoEm(),
                a.getReconhecido(),
                a.getReconhecidoEm(),
                a.getReconhecidoPor(),
                a.getEquipamento() == null ? null : a.getEquipamento().getId()
        );
    }
}
