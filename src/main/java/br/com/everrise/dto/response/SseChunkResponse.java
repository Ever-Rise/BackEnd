package br.com.everrise.dto.response;

import lombok.Builder;

@Builder
public record SseChunkResponse(
        String chunk,
        boolean done
) {
}

