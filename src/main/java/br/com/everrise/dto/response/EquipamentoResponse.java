package br.com.everrise.dto.response;

import br.com.everrise.domain.Equipamento;

import java.time.LocalDateTime;

public record EquipamentoResponse(
        Long id,
        String identificador,
        String descricao,
        String status,
        String localizacao,
        Integer bateria,
        LocalDateTime ultimaAtualizacao
) {
    public static EquipamentoResponse from(Equipamento e) {
        return new EquipamentoResponse(
                e.getId(),
                e.getIdentificador(),
                e.getDescricao(),
                e.getStatus() == null ? null : e.getStatus().name(),
                e.getLocalizacao(),
                e.getBateria(),
                e.getUltimaAtualizacao()
        );
    }
}

