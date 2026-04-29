package br.com.everrise.controller;

import br.com.everrise.dto.request.WebhookMercadoPagoRequest;
import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@RestController
@RequestMapping("/api/v1/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Recebimento de webhooks Mercado Pago")
public class PagamentoController {

    private final PedidoService pedidoService;

    @Value("${app.mercadopago.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    @Operation(summary = "Webhook Mercado Pago", description = "Recebe notificacoes de pagamento do Mercado Pago. Sempre retorna 200 para evitar retries infinitos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook processado ou ignorado por assinatura invalida",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Dados invalidos no corpo da requisicao")
    })
    public ResponseEntity<ApiResponse<String>> webhook(
            @Valid @RequestBody WebhookMercadoPagoRequest request,
            @RequestHeader(value = "X-Signature", required = false) String signature
    ) {
        if (isValidSignature(signature, request.getDataId())) {
            pedidoService.processWebhook(request.getDataId(), resolveStatus(request));
            return ResponseEntity.ok(ApiResponse.ok("processed", "Webhook processado"));
        }

        // Sempre retorna 200 para evitar retry infinito do provedor.
        return ResponseEntity.ok(ApiResponse.ok("ignored", "Webhook ignorado por assinatura invalida"));
    }

    private boolean isValidSignature(String signature, String payloadId) {
        if (signature == null || signature.isBlank()) {
            return false;
        }

        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = hmac.doFinal(payloadId.getBytes(StandardCharsets.UTF_8));
            String expected = HexFormat.of().formatHex(digest);
            return expected.equalsIgnoreCase(signature);
        } catch (Exception ex) {
            return false;
        }
    }

    private String resolveStatus(WebhookMercadoPagoRequest request) {
        if (request.getPaymentStatus() != null && !request.getPaymentStatus().isBlank()) {
            return request.getPaymentStatus();
        }

        String action = request.getAction().toLowerCase();
        if (action.contains("approved")) {
            return "approved";
        }
        if (action.contains("cancelled") || action.contains("rejected")) {
            return "cancelled";
        }
        return "pending";
    }
}
