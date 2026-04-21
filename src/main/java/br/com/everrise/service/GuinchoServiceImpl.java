package br.com.everrise.service;

import br.com.everrise.domain.entity.Guincho;
import br.com.everrise.domain.entity.TelemetryRecord;
import br.com.everrise.domain.entity.User;
import br.com.everrise.domain.enums.ComandoAcao;
import br.com.everrise.domain.enums.GuinchoStatus;
import br.com.everrise.domain.enums.UserRole;
import br.com.everrise.dto.request.ComandoRequest;
import br.com.everrise.dto.request.CreateGuinchoRequest;
import br.com.everrise.dto.request.UpdateGuinchoRequest;
import br.com.everrise.dto.response.ComandoPublicadoResponse;
import br.com.everrise.dto.response.GuinchoResponse;
import br.com.everrise.dto.response.GuinchoStatusResponse;
import br.com.everrise.dto.response.TelemetryResponse;
import br.com.everrise.dto.response.WebSocketEventResponse;
import br.com.everrise.exception.DeviceNotBoundException;
import br.com.everrise.exception.EmergencyStateException;
import br.com.everrise.exception.ResourceNotFoundException;
import br.com.everrise.mapper.GuinchoMapper;
import br.com.everrise.mapper.TelemetryMapper;
import br.com.everrise.repository.GuinchoRepository;
import br.com.everrise.repository.GuinchoSessionRepository;
import br.com.everrise.repository.TelemetryRecordRepository;
import br.com.everrise.util.MqttTopicBuilder;
import br.com.everrise.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GuinchoServiceImpl implements GuinchoService {

    private final GuinchoRepository guinchoRepository;
    private final GuinchoSessionRepository guinchoSessionRepository;
    private final TelemetryRecordRepository telemetryRecordRepository;
    private final GuinchoMapper guinchoMapper;
    private final TelemetryMapper telemetryMapper;
    private final MqttService mqttService;
    private final MqttTopicBuilder topicBuilder;
    private final SecurityUtils securityUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<GuinchoResponse> findAllAccessible() {
        User user = securityUtils.getCurrentUser();
        List<Guincho> guinchos;
        if (user.getRole() == UserRole.ROLE_ADMIN || user.getRole() == UserRole.ROLE_CLINICA) {
            guinchos = guinchoRepository.findAll().stream().filter(Guincho::getAtivo).toList();
        } else {
            guinchos = guinchoRepository.findByOwnerIdAndAtivoTrue(user.getId());
        }
        return guinchos.stream().map(guinchoMapper::toResponse).toList();
    }

    @Override
    public GuinchoResponse findByIdAccessible(Long guinchoId) {
        Guincho guincho = findAccessibleGuincho(guinchoId);
        return guinchoMapper.toResponse(guincho);
    }

    @Override
    public GuinchoStatusResponse findStatusCached(Long guinchoId) {
        String key = statusKey(guinchoId);
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof GuinchoStatusResponse response) {
            return response;
        }

        GuinchoStatusResponse response = guinchoMapper.toStatusResponse(findAccessibleGuincho(guinchoId));
        redisTemplate.opsForValue().set(key, response, 5, TimeUnit.SECONDS);
        return response;
    }

    @Override
    public List<TelemetryResponse> findTelemetry(Long guinchoId, int limit, LocalDateTime from, LocalDateTime to) {
        findAccessibleGuincho(guinchoId);
        int safeLimit = Math.min(Math.max(limit, 1), 200);

        List<TelemetryRecord> records;
        if (from != null && to != null) {
            records = telemetryRecordRepository.findByGuinchoIdAndRecordedAtBetweenOrderByRecordedAtDesc(
                    guinchoId,
                    from,
                    to,
                    PageRequest.of(0, safeLimit)
            );
        } else {
            records = telemetryRecordRepository.findByGuinchoIdOrderByRecordedAtDesc(guinchoId, PageRequest.of(0, safeLimit));
        }
        return records.stream().map(telemetryMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public GuinchoResponse create(CreateGuinchoRequest request) {
        User user = securityUtils.getCurrentUser();
        Guincho guincho = Guincho.builder()
                .serialNumber(request.getSerialNumber())
                .apelido(request.getApelido())
                .owner(user)
                .status(GuinchoStatus.DESLIGADO)
                .build();
        return guinchoMapper.toResponse(guinchoRepository.save(guincho));
    }

    @Override
    @Transactional
    public GuinchoResponse updateApelido(Long guinchoId, UpdateGuinchoRequest request) {
        Guincho guincho = findAccessibleGuincho(guinchoId);
        guincho.setApelido(request.getApelido());
        return guinchoMapper.toResponse(guinchoRepository.save(guincho));
    }

    @Override
    @Transactional
    public void softDelete(Long guinchoId) {
        Guincho guincho = findAccessibleGuincho(guinchoId);
        guincho.setAtivo(false);
        guinchoRepository.save(guincho);
    }

    @Override
    @Transactional
    public ComandoPublicadoResponse enviarComando(Long guinchoId, ComandoRequest request) {
        Guincho guincho = findAccessibleGuincho(guinchoId);
        Long userId = securityUtils.getCurrentUser().getId();

        guinchoSessionRepository.findFirstByGuinchoIdAndUserIdAndActiveTrue(guinchoId, userId)
                .orElseThrow(() -> new DeviceNotBoundException("Dispositivo nao vinculado ao guincho"));

        if (!request.getAcao().isEmergency() && guincho.getStatus() == GuinchoStatus.EMERGENCIA) {
            throw new EmergencyStateException("Guincho em emergencia, apenas EMERGENCIA_STOP permitido");
        }

        if (!request.getAcao().isEmergency()
                && guincho.getStatus() != GuinchoStatus.PRONTO
                && guincho.getStatus() != GuinchoStatus.EM_MOVIMENTO) {
            throw new EmergencyStateException("Estado atual do guincho nao permite movimentacao");
        }

        TelemetryRecord latest = telemetryRecordRepository.findFirstByGuinchoIdOrderByRecordedAtDesc(guinchoId).orElse(null);
        boolean movementCommand = request.getAcao() != ComandoAcao.PARAR && !request.getAcao().isEmergency();
        if (movementCommand && latest != null && (Boolean.TRUE.equals(latest.getAnomalyAlert()) || Boolean.TRUE.equals(latest.getObstacleDetected()))) {
            throw new EmergencyStateException("Movimento bloqueado por alerta de anomalia/obstaculo");
        }

        mqttService.publishCommand(guinchoId, request);

        GuinchoStatus nextStatus = request.getAcao().isEmergency() ? GuinchoStatus.EMERGENCIA : GuinchoStatus.EM_MOVIMENTO;
        guincho.setStatus(nextStatus);
        guincho.setIsMoving(!request.getAcao().isEmergency() && request.getAcao() != ComandoAcao.PARAR);
        guinchoRepository.save(guincho);

        GuinchoStatusResponse status = guinchoMapper.toStatusResponse(guincho);
        redisTemplate.opsForValue().set(statusKey(guinchoId), status, 5, TimeUnit.SECONDS);

        messagingTemplate.convertAndSend("/topic/guincho/" + guinchoId,
                WebSocketEventResponse.builder()
                        .event("status_update")
                        .guinchoId(guinchoId)
                        .payload(status)
                        .timestamp(LocalDateTime.now().toString())
                        .build());

        return ComandoPublicadoResponse.builder()
                .comandoId(UUID.randomUUID())
                .mqttTopic(topicBuilder.commandTopic(guinchoId))
                .publishedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public GuinchoStatusResponse currentStatus(Long guinchoId) {
        return findStatusCached(guinchoId);
    }

    @Override
    @Transactional
    public WebSocketEventResponse ativarEmergencia(Long guinchoId) {
        Guincho guincho = findAccessibleGuincho(guinchoId);
        guincho.setStatus(GuinchoStatus.EMERGENCIA);
        guincho.setIsMoving(false);
        guinchoRepository.save(guincho);

        ComandoRequest emergency = new ComandoRequest(ComandoAcao.EMERGENCIA_STOP, 0, 1);
        mqttService.publishCommand(guinchoId, emergency);

        GuinchoStatusResponse status = guinchoMapper.toStatusResponse(guincho);
        redisTemplate.opsForValue().set(statusKey(guinchoId), status, 5, TimeUnit.SECONDS);

        WebSocketEventResponse event = WebSocketEventResponse.builder()
                .event("emergencia")
                .guinchoId(guinchoId)
                .payload(java.util.Map.of("status", "EMERGENCIA", "triggeredBy", "user"))
                .timestamp(LocalDateTime.now().toString())
                .build();
        messagingTemplate.convertAndSend("/topic/guincho/" + guinchoId, event);
        return event;
    }

    private Guincho findAccessibleGuincho(Long guinchoId) {
        User user = securityUtils.getCurrentUser();
        Guincho guincho = guinchoRepository.findByIdAndAtivoTrue(guinchoId)
                .orElseThrow(() -> new ResourceNotFoundException("Guincho nao encontrado"));

        if (user.getRole() == UserRole.ROLE_ADMIN || user.getRole() == UserRole.ROLE_CLINICA) {
            return guincho;
        }

        if (!guincho.getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Guincho nao encontrado para o usuario autenticado");
        }
        return guincho;
    }

    private String statusKey(Long guinchoId) {
        return "status:" + guinchoId;
    }
}
