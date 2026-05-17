package br.com.everrise.controller;

import br.com.everrise.dto.request.ReconhecerAlertaRequest;
import br.com.everrise.dto.response.AlertaResponse;
import br.com.everrise.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
@Tag(name = "Alertas")
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    @Operation(summary = "Listar alertas não reconhecidos")
    public ResponseEntity<List<AlertaResponse>> listarNaoReconhecidos() {
        return ResponseEntity.ok(alertaService.listarNaoReconhecidos().stream().map(AlertaResponse::from).toList());
    }

    @GetMapping("/equipamento/{id}")
    @Operation(summary = "Listar alertas por equipamento")
    public ResponseEntity<List<AlertaResponse>> listarPorEquipamento(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.listarPorEquipamento(id).stream().map(AlertaResponse::from).toList());
    }

    @GetMapping("/equipamento/{id}/ativos")
    @Operation(summary = "Listar alertas não reconhecidos por equipamento")
    public ResponseEntity<List<AlertaResponse>> listarNaoReconhecidosPorEquipamento(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.listarNaoReconhecidosPorEquipamento(id).stream().map(AlertaResponse::from).toList());
    }

    @PatchMapping("/{id}/reconhecer")
    @Operation(summary = "Reconhecer alerta")
    public ResponseEntity<AlertaResponse> reconhecer(@PathVariable Long id, @Valid @RequestBody ReconhecerAlertaRequest request) {
        return ResponseEntity.ok(AlertaResponse.from(alertaService.reconhecer(id, request.usuarioId())));
    }
}
