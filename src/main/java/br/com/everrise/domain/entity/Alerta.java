package br.com.everrise.domain.entity;

import br.com.everrise.domain.enums.TipoAlerta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
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
    name = "alertas",
    indexes = {
        @Index(name = "idx_alerta_guincho_reconhecido", columnList = "guincho_id, reconhecido"),
        @Index(name = "idx_alerta_criado_em", columnList = "criado_em")
    }
)
public class Alerta extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guincho_id", nullable = false)
    private Guincho guincho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoAlerta tipo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean reconhecido = false;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "reconhecido_em")
    private LocalDateTime reconhecidoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reconhecido_por")
    private User reconhecidoPor;

    @Column(length = 255)
    private String descricao;
}
