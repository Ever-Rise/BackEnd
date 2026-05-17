package br.com.everrise.service;

import br.com.everrise.domain.SessaoUso;
import br.com.everrise.domain.Telemetria;
import br.com.everrise.repository.TelemetriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;
    private final EquipamentoService equipamentoService;

    @Transactional
    public Telemetria registrar(Long equipamentoId, Long sessaoId, String payload) {
        var equipamento = equipamentoService.buscarPorId(equipamentoId);
        Telemetria telemetria = Telemetria.builder()
                .equipamento(equipamento)
                .sessao(sessaoId == null ? null : SessaoUso.builder().id(sessaoId).build())
                .timestamp(LocalDateTime.now())
                .payload(payload)
                .build();
        return telemetriaRepository.save(telemetria);
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
}

