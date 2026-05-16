package br.com.everrise.dto.response;

import br.com.everrise.domain.SessaoUso;

import java.time.LocalDateTime;

public record SessaoUsoResponse(
        Long id,
        Long equipamentoId,
        String identificadorEquipamento,
        Long operadorId,
        Long pacienteId,
        String status,
        LocalDateTime iniciadaEm,
        LocalDateTime encerradaEm
) {
    public static SessaoUsoResponse from(SessaoUso s) {
        return new SessaoUsoResponse(
                s.getId(),
                s.getEquipamento() == null ? null : s.getEquipamento().getId(),
                s.getEquipamento() == null ? null : s.getEquipamento().getIdentificador(),
                s.getOperador() == null ? null : s.getOperador().getId(),
                s.getPaciente() == null ? null : s.getPaciente().getId(),
                s.getStatus() == null ? null : s.getStatus().name(),
                s.getIniciadaEm(),
                s.getEncerradaEm()
        );
    }
}

