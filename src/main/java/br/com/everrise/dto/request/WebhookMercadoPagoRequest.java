package br.com.everrise.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookMercadoPagoRequest {

    @NotBlank
    private String action;

    private String type;

    @NotBlank
    private String dataId;

    private String paymentStatus;
}
