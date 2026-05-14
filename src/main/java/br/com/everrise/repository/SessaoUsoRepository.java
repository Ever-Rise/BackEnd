package br.com.everrise.repository;

import br.com.everrise.domain.SessaoUso;
import br.com.everrise.domain.enums.StatusSessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessaoUsoRepository extends JpaRepository<SessaoUso, Long> {

    List<SessaoUso> findAllByEquipamentoId(Long equipamentoId);

    List<SessaoUso> findAllByOperadorId(Long operadorId);

    List<SessaoUso> findAllByPacienteId(Long pacienteId);

    List<SessaoUso> findAllByStatus(StatusSessao status);

    @Query("SELECT s FROM SessaoUso s WHERE s.equipamento.id = :equipamentoId AND s.status = br.com.everrise.domain.enums.StatusSessao.ATIVA")
    Optional<SessaoUso> findSessaoAtivaByEquipamento(@Param("equipamentoId") Long equipamentoId);
}

