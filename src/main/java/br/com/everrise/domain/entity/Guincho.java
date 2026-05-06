package br.com.everrise.domain.entity;

import br.com.everrise.domain.enums.GuinchoStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "guinchos")
public class Guincho extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String serialNumber;

    @Column(length = 100)
    private String apelido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GuinchoStatus status = GuinchoStatus.DESLIGADO;

    @Column(nullable = false)
    @Builder.Default
    private Integer battery = 0;

    @Column(nullable = false, name = "connection_quality")
    @Builder.Default
    private Integer connectionQuality = 0;

    @Column(nullable = false, name = "is_moving")
    @Builder.Default
    private Boolean isMoving = false;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "mqtt_client_id", length = 100)
    private String mqttClientId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "guincho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<GuinchoSession> activeSessions = new ArrayList<>();

    @OneToMany(mappedBy = "guincho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TelemetryRecord> telemetryRecords = new ArrayList<>();
}
