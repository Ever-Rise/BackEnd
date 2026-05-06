package br.com.everrise.dto.response;

import lombok.Builder;

@Builder
public record WebSocketEventResponse(
        String event,
        Long guinchoId,
        Object payload,
        String timestamp
) {
}

