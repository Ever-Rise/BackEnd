package br.com.everrise.controller;

import br.com.everrise.dto.request.BindDeviceRequest;
import br.com.everrise.dto.request.LoginRequest;
import br.com.everrise.dto.request.RefreshTokenRequest;
import br.com.everrise.dto.request.RegisterRequest;
import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.dto.response.AuthResponse;
import br.com.everrise.dto.response.GuinchoSessionResponse;
import br.com.everrise.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Realizar login", description = "Autentica um usuario com email e senha, retornando tokens JWT")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = br.com.everrise.dto.response.ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciais invalidas"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados invalidos no corpo da requisicao")
    })
    public ResponseEntity<br.com.everrise.dto.response.ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(br.com.everrise.dto.response.ApiResponse.ok(authService.login(request), "Login realizado com sucesso"));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuario", description = "Cria uma nova conta de usuario com email e senha")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cadastro realizado com sucesso",
                    content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email ja existe"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    public ResponseEntity<br.com.everrise.dto.response.ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(br.com.everrise.dto.response.ApiResponse.ok(authService.register(request), "Cadastro realizado com sucesso"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token JWT", description = "Renova o access token usando o refresh token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token atualizado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token invalido ou expirado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    public ResponseEntity<br.com.everrise.dto.response.ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(br.com.everrise.dto.response.ApiResponse.ok(authService.refresh(request), "Token atualizado com sucesso"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Fazer logout", description = "Realiza o logout e invalida o token JWT")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Logout realizado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token invalido")
    })
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/device/bind")
    @Operation(summary = "Vincular dispositivo", description = "Vincula um guincho (dispositivo IoT) a uma sessao de usuario")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dispositivo vinculado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Dispositivo nao encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Dispositivo ja vinculado a outro usuario")
    })
    public ResponseEntity<br.com.everrise.dto.response.ApiResponse<GuinchoSessionResponse>> bindDevice(@Valid @RequestBody BindDeviceRequest request) {
        return ResponseEntity.ok(br.com.everrise.dto.response.ApiResponse.ok(authService.bindDevice(request), "Dispositivo vinculado com sucesso"));
    }

    @DeleteMapping("/device/unbind/{guinchoId}")
    @Operation(summary = "Desvincular dispositivo", description = "Remove a vinculacao de um guincho da sessao do usuario")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dispositivo desvinculado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Dispositivo nao encontrado")
    })
    public ResponseEntity<br.com.everrise.dto.response.ApiResponse<Void>> unbindDevice(@PathVariable Long guinchoId) {
        authService.unbindDevice(guinchoId);
        return ResponseEntity.ok(br.com.everrise.dto.response.ApiResponse.ok(null, "Dispositivo desvinculado com sucesso"));
    }
}

