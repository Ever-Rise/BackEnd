package br.com.everrise.controller;

import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.dto.response.TelemetryResponse;
import br.com.everrise.service.TelemetryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Obter historico de telemetria paginado", description = "Retorna historico de telemetria de um guincho com paginacao e filtros opcionais por data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historico carregado com sucesso",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuario nao autenticado")
    })
    public ResponseEntity<ApiResponse<Page<TelemetryResponse>>> history(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Quantidade maxima de registros a retornar", example = "50")
            @RequestParam(defaultValue = "50") int limit,
            @Parameter(description = "Data/hora inicial do filtro (ISO 8601)", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) LocalDateTime from,
            @Parameter(description = "Data/hora final do filtro (ISO 8601)", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) LocalDateTime to
    ) {
        return ResponseEntity.ok(ApiResponse.ok(telemetryService.history(id, limit, from, to), "Historico de telemetria carregado"));
    }

    @GetMapping("/guincho/{id}/latest")
    @Operation(summary = "Obter ultima telemetria", description = "Retorna o ultimo registro de telemetria de um guincho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ultima telemetria carregada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TelemetryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado ou sem registros de telemetria"),
            @ApiResponse(responseCode = "401", description = "Usuario nao autenticado")
    })
    public ResponseEntity<ApiResponse<TelemetryResponse>> latest(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(telemetryService.latest(id), "Ultima telemetria carregada"));
    }

    @GetMapping("/guincho/{id}/alerts")
    @Operation(summary = "Obter alertas de telemetria", description = "Retorna lista de registros de telemetria que representam alertas/anomalias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alertas carregados com sucesso",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TelemetryResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuario nao autenticado")
    })
    public ResponseEntity<ApiResponse<List<TelemetryResponse>>> alerts(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Quantidade maxima de alertas a retornar", example = "50")
            @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(telemetryService.alerts(id, limit), "Alertas de telemetria carregados"));
    }
}
