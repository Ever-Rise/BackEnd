package br.com.everrise.repository;

import br.com.everrise.domain.entity.Plano;
import br.com.everrise.domain.enums.TipoPlano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PlanoRepository extends JpaRepository<Plano, Long>, JpaSpecificationExecutor<Plano> {

    Optional<Plano> findByTipo(TipoPlano tipo);
}

