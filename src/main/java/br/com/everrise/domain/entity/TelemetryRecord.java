package br.com.everrise.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "telemetry_records",
        indexes = {
                @Index(name = "idx_telemetry_guincho_recorded_at", columnList = "guincho_id, recorded_at")
        }
)
public class TelemetryRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guincho_id", nullable = false)
    private Guincho guincho;

    @Column(name = "fsr_reading")
    private Double fsrReading;

    @Column(nullable = false, name = "obstacle_detected")
    @Builder.Default
    private Boolean obstacleDetected = false;

    @Column(nullable = false, name = "anomaly_alert")
    @Builder.Default
    private Boolean anomalyAlert = false;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "connection_quality")
    private Integer connectionQuality;

    @Lob
    @Column(name = "raw_payload")
    private String rawPayload;

    @Column(nullable = false, name = "recorded_at")
    private LocalDateTime recordedAt;
}
