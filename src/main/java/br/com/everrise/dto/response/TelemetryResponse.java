package br.com.everrise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryResponse {
    private Long id;
    private Long guinchoId;
    private Double fsrReading;
    private Boolean obstacleDetected;
    private Boolean anomalyAlert;
    private Integer batteryLevel;
    private Integer connectionQuality;
    private LocalDateTime recordedAt;
}
