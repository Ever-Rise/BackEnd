package br.com.everrise.dto.response;

import br.com.everrise.domain.enums.TipoAlerta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaResponse {

    private Long id;
    private Long guinchoId;
    private String guinchoApelido;
    private TipoAlerta tipo;
    private String descricao;
    private Boolean reconhecido;
    private LocalDateTime criadoEm;
    private LocalDateTime reconhecidoEm;
    private String reconhecidoPorNome;
}
