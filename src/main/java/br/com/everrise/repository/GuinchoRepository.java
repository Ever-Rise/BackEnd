package br.com.everrise.repository;

import br.com.everrise.domain.entity.Guincho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuinchoRepository extends JpaRepository<Guincho, Long>, JpaSpecificationExecutor<Guincho> {

    Optional<Guincho> findBySerialNumber(String serialNumber);

    List<Guincho> findByOwnerIdAndAtivoTrue(Long ownerId);

    Optional<Guincho> findByIdAndAtivoTrue(Long id);

    @Query("SELECT COUNT(g) FROM Guincho g WHERE g.owner.id = :ownerId AND g.ativo = true AND g.id != :guinchoId")
    long countByOwnerIdAndAtivoTrueAndIdNot(@Param("ownerId") Long ownerId, @Param("guinchoId") Long guinchoId);

    @Query("SELECT COUNT(g) FROM Guincho g WHERE g.owner.id = :ownerId AND g.ativo = true")
    long countByOwnerIdAndAtivoTrue(@Param("ownerId") Long ownerId);
}
