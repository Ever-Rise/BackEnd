package br.com.everrise.mapper;

import br.com.everrise.domain.entity.Alerta;
import br.com.everrise.dto.response.AlertaResponse;
import org.springframework.stereotype.Component;

@Component
public class AlertaMapper {

    public AlertaResponse toResponse(Alerta alerta) {
        if (alerta == null) {
            return null;
        }

        return AlertaResponse.builder()
            .id(alerta.getId())
            .guinchoId(alerta.getGuincho().getId())
            .guinchoApelido(alerta.getGuincho().getApelido())
            .tipo(alerta.getTipo())
            .descricao(alerta.getDescricao())
            .reconhecido(alerta.getReconhecido())
            .criadoEm(alerta.getCriadoEm())
            .reconhecidoEm(alerta.getReconhecidoEm())
            .reconhecidoPorNome(alerta.getReconhecidoPor() != null ? alerta.getReconhecidoPor().getName() : null)
            .build();
    }
}
