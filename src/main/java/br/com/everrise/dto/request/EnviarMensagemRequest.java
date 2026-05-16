package br.com.everrise.dto.request;

import br.com.everrise.domain.enums.RemetenteMensagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnviarMensagemRequest(
        @NotNull RemetenteMensagem remetente,
        @NotBlank String conteudo
) {
}

