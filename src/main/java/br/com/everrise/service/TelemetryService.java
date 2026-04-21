package br.com.everrise.service;

import br.com.everrise.dto.response.TelemetryResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TelemetryService {

    void processIncomingTelemetry(Long guinchoId, Map<String, Object> payload);

    Page<TelemetryResponse> history(Long guinchoId, int limit, LocalDateTime from, LocalDateTime to);

    TelemetryResponse latest(Long guinchoId);

    List<TelemetryResponse> alerts(Long guinchoId, int limit);
}
