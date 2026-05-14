package br.com.everrise.repository;

import br.com.everrise.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByEmail(String email);

    List<Paciente> findAllByAtivoTrue();

    @Query("SELECT p FROM Paciente p JOIN p.familiares f WHERE f.id = :idUsuario")
    List<Paciente> findPacientesByFamiliarId(@Param("idUsuario") Long idUsuario);
}


