package br.com.everrise.service;

import br.com.everrise.dto.request.ValidarTokenDescontoRequest;
import br.com.everrise.dto.response.ValidacaoTokenDescontoResponse;

import java.math.BigDecimal;

public interface TokenDescontoService {

    /**
     * Valida um token JWT de desconto
     * @param request Contém o token e planoId para validação
     * @return Response com status de validação e percentual de desconto se válido
     */
    ValidacaoTokenDescontoResponse validarToken(ValidarTokenDescontoRequest request);

    /**
     * Gera um token de desconto JWT
     * @param planoId ID do plano
     * @param percentualDesconto Percentual de desconto (0-100)
     * @return Token JWT
     */
    String gerarToken(Long planoId, BigDecimal percentualDesconto);
}
