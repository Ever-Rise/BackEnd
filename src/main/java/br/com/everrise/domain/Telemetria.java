package br.com.everrise.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "telemetria")
public class Telemetria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_telemetria")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_equipamento", nullable = false)
    private Equipamento equipamento;

    @ManyToOne
    @JoinColumn(name = "id_sessao")
    private SessaoUso sessao;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;
}

