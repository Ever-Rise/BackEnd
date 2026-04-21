package br.com.everrise.dto.request;

import br.com.everrise.domain.enums.ComandoAcao;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComandoRequest {

    @NotNull
    private ComandoAcao acao;

    @Min(0)
    @Max(100)
    private Integer velocidade;

    @Min(1)
    private Integer duracao;
}
