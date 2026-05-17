package br.com.everrise.repository;

import br.com.everrise.domain.TokenDesconto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenDescontoRepository extends JpaRepository<TokenDesconto, Long> {

    Optional<TokenDesconto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<TokenDesconto> findAllByPacienteId(Long pacienteId);

    @Query("SELECT t FROM TokenDesconto t WHERE t.paciente.id = :pacienteId AND t.utilizado = false AND t.expiraEm > :agora")
    List<TokenDesconto> findTokensValidosByPaciente(@Param("pacienteId") Long pacienteId, @Param("agora") LocalDateTime agora);
}

