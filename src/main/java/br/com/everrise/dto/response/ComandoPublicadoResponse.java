package br.com.everrise.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ComandoPublicadoResponse(
        UUID comandoId,
        String mqttTopic,
        LocalDateTime publishedAt
) {
}

