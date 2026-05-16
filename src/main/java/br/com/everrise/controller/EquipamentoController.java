package br.com.everrise.controller;

import br.com.everrise.domain.Equipamento;
import br.com.everrise.dto.request.AtualizarStatusRequest;
import br.com.everrise.dto.request.EquipamentoRequest;
import br.com.everrise.dto.response.EquipamentoResponse;
import br.com.everrise.service.EquipamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/equipamentos")
@RequiredArgsConstructor
@Tag(name = "Equipamentos")
public class EquipamentoController {

    private final EquipamentoService equipamentoService;

    @GetMapping
    @Operation(summary = "Listar equipamentos")
    public ResponseEntity<List<EquipamentoResponse>> listarTodos() {
        return ResponseEntity.ok(equipamentoService.listarTodos().stream().map(EquipamentoResponse::from).toList());
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Listar equipamentos disponíveis")
    public ResponseEntity<List<EquipamentoResponse>> listarDisponiveis() {
        return ResponseEntity.ok(equipamentoService.listarDisponiveis().stream().map(EquipamentoResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar equipamento por ID")
    public ResponseEntity<EquipamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(EquipamentoResponse.from(equipamentoService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar equipamento")
    public ResponseEntity<EquipamentoResponse> criar(@Valid @RequestBody EquipamentoRequest request) {
        Equipamento equipamento = Equipamento.builder()
                .identificador(request.identificador())
                .descricao(request.descricao())
                .localizacao(request.localizacao())
                .ultimaAtualizacao(LocalDateTime.now())
                .build();
        return ResponseEntity.status(201).body(EquipamentoResponse.from(equipamentoService.criar(equipamento)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do equipamento")
    public ResponseEntity<EquipamentoResponse> atualizarStatus(@PathVariable Long id, @Valid @RequestBody AtualizarStatusRequest request) {
        return ResponseEntity.ok(EquipamentoResponse.from(equipamentoService.atualizarStatus(id, request.status())));
    }
}

