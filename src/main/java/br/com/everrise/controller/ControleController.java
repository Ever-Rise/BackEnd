package br.com.everrise.controller;

import br.com.everrise.dto.request.ComandoRequest;
import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.dto.response.ComandoPublicadoResponse;
import br.com.everrise.dto.response.GuinchoStatusResponse;
import br.com.everrise.dto.response.WebSocketEventResponse;
import br.com.everrise.security.ResourceOwnershipService;
import br.com.everrise.service.GuinchoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final ResourceOwnershipService ownershipService;

    @PostMapping("/{id}/comando")
    @Operation(summary = "Enviar comando de movimentacao", description = "Publica um comando MQTT para o guincho realizar uma acao de movimentacao")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Comando publicado no broker MQTT com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComandoPublicadoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Guincho ocupado ou em emergencia")
    })
    public ResponseEntity<ApiResponse<ComandoPublicadoResponse>> enviarComando(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ComandoRequest request
    ) {
        try {
            ownershipService.validarAcessoGuincho(id);
            ComandoPublicadoResponse response = guinchoService.enviarComando(id, request);
            return ResponseEntity.accepted().body(ApiResponse.ok(response, "Comando publicado no broker MQTT"));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).body(ApiResponse.error("Acesso negado"));
        }
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Obter status atual do guincho", description = "Retorna o status em tempo real do guincho (posicao, bateria, etc)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status carregado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GuinchoStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado")
    })
    public ResponseEntity<ApiResponse<GuinchoStatusResponse>> status(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.currentStatus(id), "Status carregado"));
    }

    @PostMapping("/{id}/emergencia")
    @Operation(summary = "Ativar emergencia", description = "Ativa o modo de emergencia do guincho, interrompendo operacoes em andamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emergencia ativada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WebSocketEventResponse.class))),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Guincho ja em estado de emergencia")
    })
    public ResponseEntity<ApiResponse<WebSocketEventResponse>> emergencia(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id) {
        try {
            ownershipService.validarAcessoGuincho(id);
            WebSocketEventResponse event = guinchoService.ativarEmergencia(id);
            return ResponseEntity.ok(ApiResponse.ok(event, "Emergencia ativada"));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).body(ApiResponse.error("Acesso negado"));
        }
    }
}
}
