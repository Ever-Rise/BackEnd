package br.com.everrise.controller;

import br.com.everrise.dto.request.CheckoutRequest;
import br.com.everrise.dto.response.PedidoResponse;
import br.com.everrise.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gerenciar pedidos e checkout de assinaturas")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping("/checkout")
    @Operation(summary = "Realizar checkout de pedido", description = "Cria um novo pedido e inicia o processo de pagamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos ou plano nao encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuario nao autenticado")
    })
    public ResponseEntity<PedidoResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(pedidoService.checkout(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Listar meus pedidos", description = "Retorna lista com todos os pedidos do usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos carregados com sucesso",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Usuario nao autenticado")
    })
    public ResponseEntity<List<PedidoResponse>> myOrders() {
        return ResponseEntity.ok(pedidoService.myOrders());
    }
}

