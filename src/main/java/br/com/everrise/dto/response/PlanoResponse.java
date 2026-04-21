package br.com.everrise.dto.response;

import br.com.everrise.domain.enums.TipoPlano;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanoResponse {
    private Long id;
    private TipoPlano tipo;
    private BigDecimal preco;
    private Integer maxDispositivos;
    private String descricao;
}

