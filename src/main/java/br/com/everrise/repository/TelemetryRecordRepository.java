package br.com.everrise.repository;

import br.com.everrise.domain.entity.TelemetryRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TelemetryRecordRepository extends JpaRepository<TelemetryRecord, Long>, JpaSpecificationExecutor<TelemetryRecord> {

    List<TelemetryRecord> findByGuinchoIdOrderByRecordedAtDesc(Long guinchoId, Pageable pageable);

    Optional<TelemetryRecord> findFirstByGuinchoIdOrderByRecordedAtDesc(Long guinchoId);

    List<TelemetryRecord> findByGuinchoIdAndRecordedAtBetweenOrderByRecordedAtDesc(Long guinchoId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<TelemetryRecord> findByGuinchoIdAndAnomalyAlertTrueOrderByRecordedAtDesc(Long guinchoId, Pageable pageable);
}
