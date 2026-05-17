package br.com.everrise.repository;

import br.com.everrise.domain.Alerta;
import br.com.everrise.domain.enums.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findAllByEquipamentoId(Long equipamentoId);

    List<Alerta> findAllBySessaoId(Long sessaoId);

    List<Alerta> findAllByReconhecidoFalse();

    List<Alerta> findAllByTipo(TipoAlerta tipo);

    @Query("SELECT a FROM Alerta a WHERE a.equipamento.id = :equipamentoId AND a.reconhecido = false ORDER BY a.geradoEm DESC")
    List<Alerta> findNaoReconhecidosByEquipamento(@Param("equipamentoId") Long equipamentoId);
}
