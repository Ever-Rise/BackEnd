package br.com.everrise.controller;

import br.com.everrise.dto.request.GerarTokenRequest;
import br.com.everrise.dto.response.TokenDescontoResponse;
import br.com.everrise.service.TokenDescontoService;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;

@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
@Tag(name = "Tokens de Desconto")
public class TokenDescontoController {

    private final TokenDescontoService tokenDescontoService;

    @PostMapping
    @Operation(summary = "Gerar token de desconto")
    public ResponseEntity<TokenDescontoResponse> gerar(@Valid @RequestBody GerarTokenRequest request) {
        return ResponseEntity.status(201).body(TokenDescontoResponse.from(
                tokenDescontoService.gerar(request.pacienteId(), request.desconto(), request.validadeDias())
        ));
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Buscar token por código")
    public ResponseEntity<TokenDescontoResponse> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(TokenDescontoResponse.from(tokenDescontoService.buscarPorCodigo(codigo)));
    }

    @PostMapping("/{codigo}/utilizar")
    @Operation(summary = "Utilizar token")
    public ResponseEntity<TokenDescontoResponse> utilizar(@PathVariable String codigo) {
        return ResponseEntity.ok(TokenDescontoResponse.from(tokenDescontoService.utilizar(codigo)));
    }

    @GetMapping("/paciente/{id}/validos")
    @Operation(summary = "Listar tokens válidos por paciente")
    public ResponseEntity<List<TokenDescontoResponse>> listarValidosPorPaciente(@PathVariable Long id) {
        return ResponseEntity.ok(tokenDescontoService.listarValidosPorPaciente(id).stream().map(TokenDescontoResponse::from).toList());
    }
}

