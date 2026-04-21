package br.com.everrise.controller;

import br.com.everrise.dto.request.CreateGuinchoRequest;
import br.com.everrise.dto.request.UpdateGuinchoRequest;
import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.dto.response.GuinchoResponse;
import br.com.everrise.dto.response.GuinchoStatusResponse;
import br.com.everrise.dto.response.TelemetryResponse;
import br.com.everrise.service.GuinchoService;
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
    public ResponseEntity<ApiResponse<List<GuinchoResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.findAllAccessible(), "Guinchos carregados"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GuinchoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.findByIdAccessible(id), "Guincho encontrado"));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<GuinchoStatusResponse>> status(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.findStatusCached(id), "Status carregado"));
    }

    @GetMapping("/{id}/telemetry")
    public ResponseEntity<ApiResponse<List<TelemetryResponse>>> telemetry(
            @PathVariable Long id,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to
    ) {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.findTelemetry(id, limit, from, to), "Telemetria carregada"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GuinchoResponse>> create(@Valid @RequestBody CreateGuinchoRequest request) {
        GuinchoResponse created = guinchoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Guincho cadastrado"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<GuinchoResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdateGuinchoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(guinchoService.updateApelido(id, request), "Guincho atualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        guinchoService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Guincho desativado com sucesso"));
    }
}
