package br.com.everrise.repository;

import br.com.everrise.domain.Equipamento;
import br.com.everrise.domain.enums.StatusEquipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    Optional<Equipamento> findByIdentificador(String identificador);

    boolean existsByIdentificador(String identificador);

    List<Equipamento> findAllByStatus(StatusEquipamento status);

    @Query("SELECT e FROM Equipamento e WHERE e.status IN :statuses")
    List<Equipamento> findDisponiveis(@Param("statuses") List<StatusEquipamento> statuses);
}

