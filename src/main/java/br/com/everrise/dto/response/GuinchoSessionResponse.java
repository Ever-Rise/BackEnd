package br.com.everrise.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GuinchoSessionResponse(
        Long id,
        Long guinchoId,
        Long userId,
        String deviceId,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Boolean active
) {
}

