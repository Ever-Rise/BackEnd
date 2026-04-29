package br.com.everrise.controller;

import br.com.everrise.dto.request.CreateGuinchoRequest;
import br.com.everrise.dto.request.UpdateGuinchoRequest;
import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.dto.response.GuinchoResponse;
import br.com.everrise.dto.response.GuinchoStatusResponse;
import br.com.everrise.dto.response.TelemetryResponse;
import br.com.everrise.service.GuinchoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/guinchos")
@RequiredArgsConstructor
@Tag(name = "Guinchos", description = "Operacoes de cadastro, estado e telemetria de guinchos")
public class GuinchoController {

    private final GuinchoService guinchoService;

    @GetMapping
    @Operation(summary = "Listar guinchos acessiveis", description = "Retorna lista de todos os guinchos que o usuario tem acesso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guinchos carregados com sucesso",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GuinchoResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token JWT invalido")
    })
    public ResponseEntity<ApiResponse<List<GuinchoResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.findAllAccessible(), "Guinchos carregados"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar guincho por ID", description = "Retorna os detalhes de um guincho especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guincho encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GuinchoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario sem acesso ao guincho")
    })
    public ResponseEntity<ApiResponse<GuinchoResponse>> findById(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.findByIdAccessible(id), "Guincho encontrado"));
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Obter status do guincho", description = "Retorna o status atual do guincho em tempo real (cached)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status carregado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GuinchoStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado")
    })
    public ResponseEntity<ApiResponse<GuinchoStatusResponse>> status(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.findStatusCached(id), "Status carregado"));
    }

    @GetMapping("/{id}/telemetry")
    @Operation(summary = "Obter telemetria do guincho", description = "Retorna historico de telemetria com filtros opcionais por data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Telemetria carregada com sucesso",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TelemetryResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado")
    })
    public ResponseEntity<ApiResponse<List<TelemetryResponse>>> telemetry(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Quantidade maxima de registros a retornar", example = "50")
            @RequestParam(defaultValue = "50") int limit,
            @Parameter(description = "Data/hora inicial do filtro (ISO 8601)")
            @RequestParam(required = false) LocalDateTime from,
            @Parameter(description = "Data/hora final do filtro (ISO 8601)")
            @RequestParam(required = false) LocalDateTime to
    ) {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.findTelemetry(id, limit, from, to), "Telemetria carregada"));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo guincho", description = "Cria um novo guincho no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Guincho cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GuinchoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT invalido")
    })
    public ResponseEntity<ApiResponse<GuinchoResponse>> create(@Valid @RequestBody CreateGuinchoRequest request) {
        GuinchoResponse created = guinchoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Guincho cadastrado"));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar guincho", description = "Atualiza informacoes do guincho (apelido, etc)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guincho atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GuinchoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    public ResponseEntity<ApiResponse<GuinchoResponse>> update(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateGuinchoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.updateApelido(id, request), "Guincho atualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar guincho", description = "Realiza soft delete de um guincho (marca como inativo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guincho desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Guincho nao encontrado")
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID do guincho", example = "1", required = true)
            @PathVariable Long id) {
        guinchoService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Guincho desativado com sucesso"));
    }
}
