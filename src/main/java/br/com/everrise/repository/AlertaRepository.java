package br.com.everrise.repository;

import br.com.everrise.domain.entity.Alerta;
import br.com.everrise.domain.enums.TipoAlerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    @Query("SELECT a FROM Alerta a WHERE a.guincho.id = :guinchoId ORDER BY a.criadoEm DESC")
    List<Alerta> findByGuinchoIdOrderByDataDesc(@Param("guinchoId") Long guinchoId);

    @Query("SELECT a FROM Alerta a WHERE a.guincho.id = :guinchoId AND a.reconhecido = false ORDER BY a.criadoEm DESC")
    List<Alerta> findPendentessByGuinchoId(@Param("guinchoId") Long guinchoId);

    @Query("SELECT a FROM Alerta a WHERE a.guincho.id = :guinchoId AND a.tipo = :tipo AND a.reconhecido = false ORDER BY a.criadoEm DESC")
    List<Alerta> findByGuinchoIdAndTipoAndNaoReconhecido(
        @Param("guinchoId") Long guinchoId,
        @Param("tipo") TipoAlerta tipo
    );

    @Query("SELECT a FROM Alerta a WHERE a.guincho.id = :guinchoId AND a.criadoEm >= :desde ORDER BY a.criadoEm DESC")
    Page<Alerta> findHistoricoAlertasPorPeriodo(
        @Param("guinchoId") Long guinchoId,
        @Param("desde") LocalDateTime desde,
        Pageable pageable
    );

    @Query("SELECT a FROM Alerta a WHERE a.guincho.id = :guinchoId ORDER BY a.criadoEm DESC LIMIT 1")
    Alerta findUltimoAlerta(@Param("guinchoId") Long guinchoId);
}
