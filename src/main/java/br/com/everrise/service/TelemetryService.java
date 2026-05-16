package br.com.everrise.service;

import br.com.everrise.domain.SessaoUso;
import br.com.everrise.domain.Telemetria;
import br.com.everrise.repository.TelemetriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final TelemetriaRepository telemetriaRepository;
    private final EquipamentoService equipamentoService;

    @Transactional
    public void processIncomingTelemetry(Long guinchoId, Map<String, Object> payload) {
        registrar(guinchoId, null, payload == null ? null : payload.toString());
    }

    @Transactional
    public Telemetria registrar(Long equipamentoId, Long sessaoId, String payload) {
        Telemetria telemetria = Telemetria.builder()
                .equipamento(equipamentoService.buscarPorId(equipamentoId))
                .sessao(sessaoId == null ? null : SessaoUso.builder().id(sessaoId).build())
                .timestamp(LocalDateTime.now())
                .payload(payload)
                .build();
        return telemetriaRepository.save(telemetria);
    }

    public Page<br.com.everrise.dto.response.TelemetryResponse> history(Long guinchoId, int limit, LocalDateTime from, LocalDateTime to) {
        int safeLimit = Math.max(1, limit);
        List<Telemetria> telemetrias = telemetriaRepository.findAllByEquipamentoId(guinchoId).stream()
                .filter(item -> from == null || !item.getTimestamp().isBefore(from))
                .filter(item -> to == null || !item.getTimestamp().isAfter(to))
                .sorted(Comparator.comparing(Telemetria::getTimestamp).reversed())
                .limit(safeLimit)
                .toList();

        List<br.com.everrise.dto.response.TelemetryResponse> responses = telemetrias.stream()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(responses, PageRequest.of(0, safeLimit), responses.size());
    }

    public br.com.everrise.dto.response.TelemetryResponse latest(Long guinchoId) {
        return telemetriaRepository.findLatestByEquipamento(guinchoId, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Nenhuma telemetria encontrada para o equipamento"));
    }

    public List<br.com.everrise.dto.response.TelemetryResponse> alerts(Long guinchoId, int limit) {
        int safeLimit = Math.max(1, limit);
        return telemetriaRepository.findLatestByEquipamento(guinchoId, PageRequest.of(0, safeLimit))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<Telemetria> listarPorEquipamento(Long equipamentoId) {
        return telemetriaRepository.findAllByEquipamentoId(equipamentoId);
    }

    public List<Telemetria> listarPorSessao(Long sessaoId) {
        return telemetriaRepository.findAllBySessaoId(sessaoId);
    }

    public List<Telemetria> buscarUltimasTelemetrias(Long equipamentoId, int quantidade) {
        return telemetriaRepository.findLatestByEquipamento(equipamentoId, PageRequest.of(0, Math.max(1, quantidade)))
                .getContent();
    }

    private br.com.everrise.dto.response.TelemetryResponse toResponse(Telemetria telemetria) {
        return br.com.everrise.dto.response.TelemetryResponse.builder()
                .id(telemetria.getId())
                .guinchoId(telemetria.getEquipamento() == null ? null : telemetria.getEquipamento().getId())
                .recordedAt(telemetria.getTimestamp())
                .build();
    }
}
