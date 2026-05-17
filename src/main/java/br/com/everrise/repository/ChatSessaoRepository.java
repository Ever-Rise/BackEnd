package br.com.everrise.repository;

import br.com.everrise.domain.ChatSessao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatSessaoRepository extends JpaRepository<ChatSessao, Long> {

    List<ChatSessao> findAllByPacienteId(Long pacienteId);

    List<ChatSessao> findAllByUsuarioId(Long usuarioId);

    @Query("SELECT c FROM ChatSessao c WHERE c.paciente.id = :pacienteId ORDER BY c.criadaEm DESC")
    Page<ChatSessao> findUltimaSessaoByPaciente(@Param("pacienteId") Long pacienteId, Pageable pageable);
}

