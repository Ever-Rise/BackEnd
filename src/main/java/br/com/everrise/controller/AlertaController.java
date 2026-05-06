package br.com.everrise.controller;

import br.com.everrise.dto.response.AlertaResponse;
import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.service.AlertaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alertas")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "Gestão de alertas de guinchos")
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping("/guincho/{guinchoId}/pendentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<AlertaResponse>>> findPendentes(@PathVariable Long guinchoId) {
        return ResponseEntity.ok(
            ApiResponse.ok(alertaService.findPendentes(guinchoId), "Alertas pendentes carregados")
        );
    }

    @GetMapping("/guincho/{guinchoId}/historico")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<AlertaResponse>>> findHistorico(
        @PathVariable Long guinchoId,
        @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(
            ApiResponse.ok(alertaService.findHistorico(guinchoId, limit), "Histórico de alertas carregado")
        );
    }

    @GetMapping("/{alertaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<AlertaResponse>> findById(@PathVariable Long alertaId) {
        return ResponseEntity.ok(
            ApiResponse.ok(alertaService.findById(alertaId), "Alerta carregado")
        );
    }

    @PatchMapping("/{alertaId}/reconhecer")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<AlertaResponse>> reconhecer(@PathVariable Long alertaId) {
        return ResponseEntity.ok(
            ApiResponse.ok(alertaService.reconhecer(alertaId), "Alerta reconhecido")
        );
    }
}
