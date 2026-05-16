package br.com.everrise.dto.response;

import br.com.everrise.domain.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String role,
        Boolean ativo,
        LocalDateTime criadoEm
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(),
                u.getRole() == null ? null : u.getRole().name(), u.getAtivo(), u.getCriadoEm());
    }
}

