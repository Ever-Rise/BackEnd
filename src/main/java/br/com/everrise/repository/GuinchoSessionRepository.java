package br.com.everrise.repository;

import br.com.everrise.domain.entity.GuinchoSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GuinchoSessionRepository extends JpaRepository<GuinchoSession, Long>, JpaSpecificationExecutor<GuinchoSession> {

    Optional<GuinchoSession> findFirstByGuinchoIdAndActiveTrue(Long guinchoId);

    Optional<GuinchoSession> findFirstByGuinchoIdAndUserIdAndDeviceId(Long guinchoId, Long userId, String deviceId);

    Optional<GuinchoSession> findFirstByGuinchoIdAndUserIdAndActiveTrue(Long guinchoId, Long userId);
}
