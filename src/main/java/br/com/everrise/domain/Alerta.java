package br.com.everrise.domain;

import br.com.everrise.domain.enums.TipoAlerta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoAlerta tipo;

    @Column(length = 255)
    private String descricao;

    @Column(name = "gerado_em", nullable = false)
    private LocalDateTime geradoEm;

    @Column(nullable = false)
    @Builder.Default
    private Boolean reconhecido = false;

    @Column(name = "reconhecido_em")
    private LocalDateTime reconhecidoEm;

    @Column(name = "reconhecido_por")
    private Long reconhecidoPor;

    @ManyToOne
    @JoinColumn(name = "id_equipamento", nullable = false)
    private Equipamento equipamento;

    @ManyToOne
    @JoinColumn(name = "id_sessao")
    private SessaoUso sessao;
}

