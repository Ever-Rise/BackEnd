package br.com.everrise.repository;

import br.com.everrise.domain.Familiar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamiliarRepository extends JpaRepository<Familiar, Long> {

    Optional<Familiar> findByEmail(String email);

    List<Familiar> findAllByPacienteId(Long pacienteId);

    boolean existsByEmailAndPacienteId(String email, Long pacienteId);
}

