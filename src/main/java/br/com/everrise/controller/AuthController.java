package br.com.everrise.controller;

import br.com.everrise.dto.request.LoginRequest;
import br.com.everrise.dto.request.RegistroRequest;
import br.com.everrise.dto.response.TokenResponse;
import br.com.everrise.dto.response.UsuarioResponse;
import br.com.everrise.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(TokenResponse.bearer(authService.login(request.email(), request.senha())));
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registrar usuário")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(201).body(UsuarioResponse.from(
                authService.registrar(request.nome(), request.email(), request.senha(), request.role())
        ));
    }
}

