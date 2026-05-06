package br.com.everrise.dto.response;

import br.com.everrise.domain.enums.GuinchoStatus;

import java.time.LocalDateTime;

public record GuinchoStatusResponse(
        GuinchoStatus status,
        Integer battery,
        Integer connectionQuality,
        Boolean isMoving,
        LocalDateTime lastSeen
) {
}
