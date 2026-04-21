package br.com.everrise.dto.response;

import br.com.everrise.domain.enums.GuinchoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuinchoResponse {
    private Long id;
    private String serialNumber;
    private String apelido;
    private GuinchoStatus status;
    private Integer battery;
    private Integer connectionQuality;
    private Boolean isMoving;
    private LocalDateTime lastSeen;
}

