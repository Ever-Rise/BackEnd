package br.com.everrise.controller;

import br.com.everrise.dto.request.BindDeviceRequest;
import br.com.everrise.dto.request.LoginRequest;
import br.com.everrise.dto.request.RefreshTokenRequest;
import br.com.everrise.dto.request.RegisterRequest;
import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.dto.response.AuthResponse;
import br.com.everrise.dto.response.GuinchoSessionResponse;
import br.com.everrise.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticacao, sessao e vinculacao de dispositivo")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request), "Login realizado com sucesso"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.register(request), "Cadastro realizado com sucesso"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(request), "Token atualizado com sucesso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/device/bind")
    public ResponseEntity<ApiResponse<GuinchoSessionResponse>> bindDevice(@Valid @RequestBody BindDeviceRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.bindDevice(request), "Dispositivo vinculado com sucesso"));
    }

    @DeleteMapping("/device/unbind/{guinchoId}")
    public ResponseEntity<ApiResponse<Void>> unbindDevice(@PathVariable Long guinchoId) {
        authService.unbindDevice(guinchoId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Dispositivo desvinculado com sucesso"));
    }
}
