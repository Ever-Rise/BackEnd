package br.com.everrise.service;

import br.com.everrise.domain.entity.Guincho;
import br.com.everrise.domain.entity.TelemetryRecord;
import br.com.everrise.domain.enums.GuinchoStatus;
import br.com.everrise.domain.enums.TipoAlerta;
import br.com.everrise.dto.response.TelemetryResponse;
import br.com.everrise.exception.ResourceNotFoundException;
import br.com.everrise.mapper.TelemetryMapper;
import br.com.everrise.repository.GuinchoRepository;
import br.com.everrise.repository.TelemetryRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TelemetryServiceImpl implements TelemetryService {

    private final GuinchoRepository guinchoRepository;
    private final TelemetryRecordRepository telemetryRecordRepository;
    private final TelemetryMapper telemetryMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AlertaService alertaService;

    private static final double FSR_THRESHOLD_CRITICO = 800.0;
    private static final int BATERIA_LIMIAR_BAIXA = 20;

    @Override
    @Transactional
    public void processIncomingTelemetry(Long guinchoId, Map<String, Object> payload) {
        Guincho guincho = guinchoRepository.findById(guinchoId)
                .orElseThrow(() -> new ResourceNotFoundException("Guincho nao encontrado"));

        TelemetryRecord record = TelemetryRecord.builder()
                .guincho(guincho)
                .fsrReading(parseDouble(payload.get("fsrReading")))
                .obstacleDetected(parseBoolean(payload.get("obstacleDetected")))
                .anomalyAlert(parseBoolean(payload.get("anomalyAlert")))
                .batteryLevel(parseInt(payload.get("batteryLevel")))
                .connectionQuality(parseInt(payload.get("connectionQuality")))
                .recordedAt(LocalDateTime.now())
                .build();

        telemetryRecordRepository.save(record);

        // RN04: Implementar bloqueio automático de movimento
        // Obstacle detected ou FSR acima do threshold ou anomalia
        if (record.getObstacleDetected() || 
            (record.getFsrReading() != null && record.getFsrReading() > FSR_THRESHOLD_CRITICO) ||
            record.getAnomalyAlert()) {
            
            handleSafetyEvent(guincho, record);
        }

        // Verificar bateria baixa
        if (record.getBatteryLevel() != null && record.getBatteryLevel() < BATERIA_LIMIAR_BAIXA) {
            handleLowBatteryAlert(guincho, record);
        }

        // Verificar conexão fraca
        if (record.getConnectionQuality() != null && record.getConnectionQuality() < 30) {
            handleWeakConnectionAlert(guincho, record);
        }

        TelemetryResponse response = telemetryMapper.toResponse(record);
        redisTemplate.opsForValue().set(latestKey(guinchoId), response, 30, TimeUnit.SECONDS);
        messagingTemplate.convertAndSend("/topic/telemetry/" + guinchoId, response);
    }

    private void handleSafetyEvent(Guincho guincho, TelemetryRecord record) {
        // Se está em movimento, pausar automaticamente (SAFETY_HOLD)
        if (guincho.getStatus() == GuinchoStatus.EM_MOVIMENTO || 
            guincho.getStatus() == GuinchoStatus.PAUSADO) {
            
            guincho.setStatus(GuinchoStatus.SAFETY_HOLD);
            guinchoRepository.save(guincho);

            // Gerar alerta apropriado
            if (record.getObstacleDetected()) {
                alertaService.gerarAlerta(TipoAlerta.OBSTACULO, guincho.getId(), 
                    "Obstáculo detectado - equipamento pausado automaticamente");
            } else if (record.getFsrReading() != null && record.getFsrReading() > FSR_THRESHOLD_CRITICO) {
                alertaService.gerarAlerta(TipoAlerta.SOBRECARGA, guincho.getId(), 
                    "Sobrecarga detectada (FSR: " + record.getFsrReading() + ") - equipamento pausado automaticamente");
            } else if (record.getAnomalyAlert()) {
                alertaService.gerarAlerta(TipoAlerta.ANOMALIA, guincho.getId(), 
                    "Anomalia detectada - equipamento pausado automaticamente");
            }

            // Notificar via WebSocket
            messagingTemplate.convertAndSend("/topic/guincho/" + guincho.getId() + "/safety", 
                Map.of(
                    "event", "SAFETY_HOLD_ATIVADO",
                    "status", guincho.getStatus(),
                    "mensagem", "Equipamento em parada de segurança - requer verificação"
                ));
        }
    }

    private void handleLowBatteryAlert(Guincho guincho, TelemetryRecord record) {
        // Gerar alerta mas não pausar (apenas aviso)
        alertaService.gerarAlerta(TipoAlerta.BATERIA_BAIXA, guincho.getId(), 
            "Bateria baixa: " + record.getBatteryLevel() + "%");
    }

    private void handleWeakConnectionAlert(Guincho guincho, TelemetryRecord record) {
        // Gerar alerta mas não pausar (apenas aviso)
        alertaService.gerarAlerta(TipoAlerta.CONEXAO_FRACA, guincho.getId(), 
            "Conexão fraca: " + record.getConnectionQuality() + "%");
    }

    @Override
    public Page<TelemetryResponse> history(Long guinchoId, int limit, LocalDateTime from, LocalDateTime to) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        List<TelemetryRecord> records = (from != null && to != null)
                ? telemetryRecordRepository.findByGuinchoIdAndRecordedAtBetweenOrderByRecordedAtDesc(guinchoId, from, to, PageRequest.of(0, safeLimit))
                : telemetryRecordRepository.findByGuinchoIdOrderByRecordedAtDesc(guinchoId, PageRequest.of(0, safeLimit));

        List<TelemetryResponse> responses = records.stream().map(telemetryMapper::toResponse).toList();
        return new PageImpl<>(responses, PageRequest.of(0, safeLimit), responses.size());
    }

    @Override
    public TelemetryResponse latest(Long guinchoId) {
        Object cached = redisTemplate.opsForValue().get(latestKey(guinchoId));
        if (cached instanceof TelemetryResponse response) {
            return response;
        }

        return telemetryRecordRepository.findFirstByGuinchoIdOrderByRecordedAtDesc(guinchoId)
                .map(telemetryMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma telemetria encontrada para o guincho"));
    }

    @Override
    public List<TelemetryResponse> alerts(Long guinchoId, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return telemetryRecordRepository.findByGuinchoIdAndAnomalyAlertTrueOrderByRecordedAtDesc(
                        guinchoId,
                        PageRequest.of(0, safeLimit)
                )
                .stream()
                .map(telemetryMapper::toResponse)
                .toList();
    }

    private Double parseDouble(Object value) {
        return value == null ? null : Double.parseDouble(value.toString());
    }

    private Integer parseInt(Object value) {
        return value == null ? null : Integer.parseInt(value.toString());
    }

    private Boolean parseBoolean(Object value) {
        return value != null && Boolean.parseBoolean(value.toString());
    }

    private String latestKey(Long guinchoId) {
        return "telemetry:" + guinchoId;
    }
}
