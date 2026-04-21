package br.com.everrise.controller;

import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.dto.response.TelemetryResponse;
import br.com.everrise.service.TelemetryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/telemetry")
@RequiredArgsConstructor
@Tag(name = "Telemetry", description = "Consulta de historico, ultimo dado e alertas de telemetria")
public class TelemetryController {

    private final TelemetryService telemetryService;

    @GetMapping("/guincho/{id}/history")
    public ResponseEntity<ApiResponse<Page<TelemetryResponse>>> history(
            @PathVariable Long id,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to
    ) {
        return ResponseEntity.ok(ApiResponse.ok(telemetryService.history(id, limit, from, to), "Historico de telemetria carregado"));
    }

    @GetMapping("/guincho/{id}/latest")
    public ResponseEntity<ApiResponse<TelemetryResponse>> latest(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(telemetryService.latest(id), "Ultima telemetria carregada"));
    }

    @GetMapping("/guincho/{id}/alerts")
    public ResponseEntity<ApiResponse<List<TelemetryResponse>>> alerts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(telemetryService.alerts(id, limit), "Alertas de telemetria carregados"));
    }
}
