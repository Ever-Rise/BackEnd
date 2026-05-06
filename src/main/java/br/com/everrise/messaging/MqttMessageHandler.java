package br.com.everrise.messaging;

import br.com.everrise.domain.entity.Guincho;
import br.com.everrise.domain.entity.TelemetryRecord;
import br.com.everrise.domain.enums.GuinchoStatus;
import br.com.everrise.dto.response.WebSocketEventResponse;
import br.com.everrise.repository.GuinchoRepository;
import br.com.everrise.repository.TelemetryRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttMessageHandler {

    private final ObjectMapper objectMapper;
    private final GuinchoRepository guinchoRepository;
    private final TelemetryRecordRepository telemetryRecordRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Long, AtomicInteger> normalCounter = new ConcurrentHashMap<>();

    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public void handleTelemetry(
            Message<String> message,
            @Header(name = "mqtt_receivedTopic", required = false) String topic
    ) {
        try {
            if (topic == null || !topic.matches("guincho/\\d+/telemetry")) {
                return;
            }

            Long guinchoId = extractGuinchoId(topic);
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            processTelemetry(guinchoId, payload);
        } catch (Exception ex) {
            log.error("Falha ao processar mensagem MQTT", ex);
        }
    }

    private void processTelemetry(Long guinchoId, Map<String, Object> payload) {
        Guincho guincho = guinchoRepository.findById(guinchoId).orElse(null);
        if (guincho == null) {
            log.warn("Guincho {} nao encontrado para telemetria", guinchoId);
            return;
        }

        Integer batteryLevel = toInteger(payload.get("batteryLevel"));
        Integer connectionQuality = toInteger(payload.get("connectionQuality"));
        Boolean obstacleDetected = toBoolean(payload.get("obstacleDetected"));
        Boolean anomalyAlert = toBoolean(payload.get("anomalyAlert"));
        Double fsrReading = toDouble(payload.get("fsrReading"));

        guincho.setBattery(batteryLevel != null ? batteryLevel : guincho.getBattery());
        guincho.setConnectionQuality(connectionQuality != null ? connectionQuality : guincho.getConnectionQuality());
        guincho.setLastSeen(LocalDateTime.now());
        if (guincho.getStatus() != GuinchoStatus.EMERGENCIA && Boolean.TRUE.equals(obstacleDetected)) {
            guincho.setStatus(GuinchoStatus.PAUSADO);
        }
        guinchoRepository.save(guincho);

        boolean shouldSave = Boolean.TRUE.equals(anomalyAlert)
                || Boolean.TRUE.equals(obstacleDetected)
                || normalCounter.computeIfAbsent(guinchoId, id -> new AtomicInteger(0)).incrementAndGet() % 10 == 0;

        if (shouldSave) {
            TelemetryRecord record = TelemetryRecord.builder()
                    .guincho(guincho)
                    .fsrReading(fsrReading)
                    .obstacleDetected(Boolean.TRUE.equals(obstacleDetected))
                    .anomalyAlert(Boolean.TRUE.equals(anomalyAlert))
                    .batteryLevel(batteryLevel)
                    .connectionQuality(connectionQuality)
                    .rawPayload(toJson(payload))
                    .recordedAt(toTimestamp(payload.get("recordedAt")))
                    .build();
            telemetryRecordRepository.save(record);
        }

        redisTemplate.opsForValue().set("telemetry:" + guinchoId, payload, 30, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(anomalyAlert) || Boolean.TRUE.equals(obstacleDetected)) {
            messagingTemplate.convertAndSend(
                    "/topic/guincho/" + guinchoId,
                    WebSocketEventResponse.builder()
                            .event(Boolean.TRUE.equals(obstacleDetected) ? "obstaculo_detectado" : "sobrecarga_detectada")
                            .guinchoId(guinchoId)
                            .payload(payload)
                            .timestamp(LocalDateTime.now().toString())
                            .build()
            );
        }

        if (batteryLevel != null && batteryLevel < 20) {
            messagingTemplate.convertAndSend(
                    "/topic/guincho/" + guinchoId,
                    WebSocketEventResponse.builder()
                            .event("bateria_low")
                            .guinchoId(guinchoId)
                            .payload(Map.of("batteryLevel", batteryLevel, "threshold", 20))
                            .timestamp(LocalDateTime.now().toString())
                            .build()
            );
        }
    }

    private Long extractGuinchoId(String topic) {
        return Long.parseLong(topic.split("/")[1]);
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value.toString());
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        return Double.parseDouble(value.toString());
    }

    private Boolean toBoolean(Object value) {
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value.toString());
    }

    private LocalDateTime toTimestamp(Object value) {
        if (value == null) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(value.toString());
        } catch (Exception ex) {
            return LocalDateTime.now();
        }
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            return "{}";
        }
    }
}
