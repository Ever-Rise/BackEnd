package br.com.everrise.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotNull
    private Long planoId;

    private String cupomDesconto;

    private String tokenDesconto;  // RN10: Token JWT com 72h de validade para desconto
}


