package br.com.everrise.controller;

import br.com.everrise.dto.request.CheckoutRequest;
import br.com.everrise.dto.response.PedidoResponse;
import br.com.everrise.service.PedidoService;
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
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping("/checkout")
    public ResponseEntity<PedidoResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(pedidoService.checkout(request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PedidoResponse>> myOrders() {
        return ResponseEntity.ok(pedidoService.myOrders());
    }
}

