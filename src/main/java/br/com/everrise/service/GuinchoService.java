package br.com.everrise.service;

import br.com.everrise.dto.request.ComandoRequest;
import br.com.everrise.dto.request.CreateGuinchoRequest;
import br.com.everrise.dto.request.UpdateGuinchoRequest;
import br.com.everrise.dto.response.ComandoPublicadoResponse;
import br.com.everrise.dto.response.GuinchoResponse;
import br.com.everrise.dto.response.GuinchoStatusResponse;
import br.com.everrise.dto.response.TelemetryResponse;
import br.com.everrise.dto.response.WebSocketEventResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface GuinchoService {

    List<GuinchoResponse> findAllAccessible();

    GuinchoResponse findByIdAccessible(Long guinchoId);

    GuinchoStatusResponse findStatusCached(Long guinchoId);

    List<TelemetryResponse> findTelemetry(Long guinchoId, int limit, LocalDateTime from, LocalDateTime to);

    GuinchoResponse create(CreateGuinchoRequest request);

    GuinchoResponse updateApelido(Long guinchoId, UpdateGuinchoRequest request);

    void softDelete(Long guinchoId);

    ComandoPublicadoResponse enviarComando(Long guinchoId, ComandoRequest request);

    GuinchoStatusResponse currentStatus(Long guinchoId);

    WebSocketEventResponse ativarEmergencia(Long guinchoId);
}
