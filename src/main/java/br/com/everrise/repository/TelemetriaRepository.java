package br.com.everrise.repository;

import br.com.everrise.domain.Telemetria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TelemetriaRepository extends JpaRepository<Telemetria, Long> {

    List<Telemetria> findAllByEquipamentoId(Long equipamentoId);

    List<Telemetria> findAllBySessaoId(Long sessaoId);

    @Query("SELECT t FROM Telemetria t WHERE t.equipamento.id = :equipamentoId ORDER BY t.timestamp DESC")
    Page<Telemetria> findLatestByEquipamento(@Param("equipamentoId") Long equipamentoId, Pageable pageable);
}

