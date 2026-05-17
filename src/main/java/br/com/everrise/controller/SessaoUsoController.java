package br.com.everrise.controller;

import br.com.everrise.domain.SessaoUso;
import br.com.everrise.dto.request.IniciarSessaoRequest;
import br.com.everrise.dto.response.SessaoUsoResponse;
import br.com.everrise.service.SessaoUsoService;
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

import java.util.List;

@RestController
@RequestMapping("/sessoes")
@RequiredArgsConstructor
@Tag(name = "Sessões de Uso")
public class SessaoUsoController {

    private final SessaoUsoService sessaoUsoService;

    @PostMapping
    @Operation(summary = "Iniciar sessão")
    public ResponseEntity<SessaoUsoResponse> iniciar(@Valid @RequestBody IniciarSessaoRequest request) {
        SessaoUso sessao = sessaoUsoService.iniciar(request.equipamentoId(), request.operadorId(), request.pacienteId());
        return ResponseEntity.status(201).body(SessaoUsoResponse.from(sessao));
    }

    @PatchMapping("/{id}/encerrar")
    @Operation(summary = "Encerrar sessão")
    public ResponseEntity<SessaoUsoResponse> encerrar(@PathVariable Long id) {
        return ResponseEntity.ok(SessaoUsoResponse.from(sessaoUsoService.encerrar(id)));
    }

    @PatchMapping("/{id}/interromper")
    @Operation(summary = "Interromper sessão")
    public ResponseEntity<SessaoUsoResponse> interromper(@PathVariable Long id) {
        return ResponseEntity.ok(SessaoUsoResponse.from(sessaoUsoService.interromper(id)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sessão por ID")
    public ResponseEntity<SessaoUsoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(SessaoUsoResponse.from(sessaoUsoService.buscarPorId(id)));
    }

    @GetMapping("/equipamento/{id}")
    @Operation(summary = "Listar sessões por equipamento")
    public ResponseEntity<List<SessaoUsoResponse>> listarPorEquipamento(@PathVariable Long id) {
        return ResponseEntity.ok(sessaoUsoService.listarPorEquipamento(id).stream().map(SessaoUsoResponse::from).toList());
    }

    @GetMapping("/paciente/{id}")
    @Operation(summary = "Listar sessões por paciente")
    public ResponseEntity<List<SessaoUsoResponse>> listarPorPaciente(@PathVariable Long id) {
        return ResponseEntity.ok(sessaoUsoService.listarPorPaciente(id).stream().map(SessaoUsoResponse::from).toList());
    }
}

