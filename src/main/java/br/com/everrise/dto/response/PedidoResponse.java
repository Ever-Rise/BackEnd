package br.com.everrise.dto.response;

import br.com.everrise.domain.enums.PedidoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {
    private Long id;
    private Long userId;
    private Long planoId;
    private PedidoStatus status;
    private BigDecimal valorTotal;
    private String mercadoPagoPaymentId;
    private String cupomDesconto;
    private LocalDateTime createdAt;
}

