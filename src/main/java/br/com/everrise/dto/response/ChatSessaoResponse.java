package br.com.everrise.dto.response;

import br.com.everrise.domain.ChatSessao;

import java.time.LocalDateTime;

public record ChatSessaoResponse(
        Long id,
        Long pacienteId,
        Long usuarioId,
        LocalDateTime criadaEm,
        LocalDateTime atualizadaEm
) {
    public static ChatSessaoResponse from(ChatSessao s) {
        return new ChatSessaoResponse(
                s.getId(),
                s.getPaciente() == null ? null : s.getPaciente().getId(),
                s.getUsuario() == null ? null : s.getUsuario().getId(),
                s.getCriadaEm(),
                s.getAtualizadaEm()
        );
    }
}

