package br.com.everrise.service;

import br.com.everrise.domain.entity.Alerta;
import br.com.everrise.domain.enums.TipoAlerta;
import br.com.everrise.dto.response.AlertaResponse;

import java.util.List;

public interface AlertaService {

    Alerta gerarAlerta(TipoAlerta tipo, Long guinchoId, String descricao);

    AlertaResponse reconhecer(Long alertaId);

    List<AlertaResponse> findPendentes(Long guinchoId);

    List<AlertaResponse> findHistorico(Long guinchoId, int limit);

    AlertaResponse findById(Long alertaId);
}
