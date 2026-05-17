package br.com.everrise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequest(@NotBlank @Size(min = 8) String novaSenha) {
}

