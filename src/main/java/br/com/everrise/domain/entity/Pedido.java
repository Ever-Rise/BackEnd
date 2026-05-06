package br.com.everrise.domain.entity;

import br.com.everrise.domain.enums.PedidoStatus;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pedidos")
public class Pedido extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_id", nullable = false)
    private Plano plano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private PedidoStatus status = PedidoStatus.PENDENTE;

    @Column(nullable = false, precision = 10, scale = 2, name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "mercado_pago_payment_id", length = 100)
    private String mercadoPagoPaymentId;

    @Column(name = "mercado_pago_status", length = 50)
    private String mercadoPagoStatus;

    @Column(name = "cupom_desconto", length = 50)
    private String cupomDesconto;

    @Column(name = "desconto_aplicado", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal descontoAplicado = BigDecimal.ZERO;
}
