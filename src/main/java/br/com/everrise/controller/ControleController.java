package br.com.everrise.controller;

import br.com.everrise.dto.request.ComandoRequest;
import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.dto.response.ComandoPublicadoResponse;
import br.com.everrise.dto.response.GuinchoStatusResponse;
import br.com.everrise.dto.response.WebSocketEventResponse;
import br.com.everrise.service.GuinchoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/controle")
@RequiredArgsConstructor
@Tag(name = "Controle", description = "Comandos de movimentacao e emergencia")
public class ControleController {

    private final GuinchoService guinchoService;

    @PostMapping("/{id}/comando")
    public ResponseEntity<ApiResponse<ComandoPublicadoResponse>> enviarComando(
            @PathVariable Long id,
            @Valid @RequestBody ComandoRequest request
    ) {
        ComandoPublicadoResponse response = guinchoService.enviarComando(id, request);
        return ResponseEntity.accepted().body(ApiResponse.ok(response, "Comando publicado no broker MQTT"));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<GuinchoStatusResponse>> status(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.currentStatus(id), "Status carregado"));
    }

    @PostMapping("/{id}/emergencia")
    public ResponseEntity<ApiResponse<WebSocketEventResponse>> emergencia(@PathVariable Long id) {
        WebSocketEventResponse event = guinchoService.ativarEmergencia(id);
        return ResponseEntity.ok(ApiResponse.ok(event, "Emergencia ativada"));
    }
}
