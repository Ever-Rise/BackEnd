package br.com.everrise.dto.response;

import br.com.everrise.domain.ChatMensagem;

import java.time.LocalDateTime;

public record ChatMensagemResponse(
        Long id,
        String remetente,
        String conteudo,
        LocalDateTime enviadaEm
) {
    public static ChatMensagemResponse from(ChatMensagem m) {
        return new ChatMensagemResponse(
                m.getId(),
                m.getRemetente() == null ? null : m.getRemetente().name(),
                m.getConteudo(),
                m.getEnviadaEm()
        );
    }
}

