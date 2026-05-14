package br.com.everrise.repository;

import br.com.everrise.domain.Usuario;
import br.com.everrise.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findAllByRole(Role role);

    List<Usuario> findAllByAtivoTrue();
}

