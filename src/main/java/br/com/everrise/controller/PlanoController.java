package br.com.everrise.controller;

import br.com.everrise.dto.response.PlanoResponse;
import br.com.everrise.service.PlanoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/planos")
@RequiredArgsConstructor
@Tag(name = "Planos", description = "Catalogo de planos/assinaturas disponiveis")
public class PlanoController {

    private final PlanoService planoService;

    @GetMapping
    @Operation(summary = "Listar todos os planos", description = "Retorna uma lista com todos os planos de assinatura disponiveis no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de planos carregada com sucesso",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlanoResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token JWT invalido ou expirado")
    })
    public ResponseEntity<List<PlanoResponse>> findAll() {
        return ResponseEntity.ok(planoService.findAll());
    }
}

