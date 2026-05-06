package br.com.everrise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidacaoTokenDescontoResponse {

    private Boolean valido;

    private String mensagem;

    private BigDecimal percentualDesconto;

    private Long planoId;

    private String jti;
}
