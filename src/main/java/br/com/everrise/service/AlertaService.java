package br.com.everrise.service;

import br.com.everrise.domain.Alerta;
import br.com.everrise.domain.SessaoUso;
import br.com.everrise.domain.enums.TipoAlerta;
import br.com.everrise.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final EquipamentoService equipamentoService;

    @Transactional
    public Alerta gerar(Long equipamentoId, Long sessaoId, TipoAlerta tipo, String descricao) {
        Alerta alerta = Alerta.builder()
                .equipamento(equipamentoService.buscarPorId(equipamentoId))
                .sessao(sessaoId == null ? null : SessaoUso.builder().id(sessaoId).build())
                .tipo(tipo)
                .descricao(descricao)
                .geradoEm(LocalDateTime.now())
                .reconhecido(false)
                .build();
        return alertaRepository.save(alerta);
    }

    @Transactional
    public Alerta reconhecer(Long alertaId, Long usuarioId) {
        Alerta alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado"));

        if (Boolean.TRUE.equals(alerta.getReconhecido())) {
            throw new RuntimeException("Alerta já reconhecido");
        }

        alerta.setReconhecido(true);
        alerta.setReconhecidoEm(LocalDateTime.now());
        alerta.setReconhecidoPor(usuarioId);
        return alertaRepository.save(alerta);
    }

    public List<Alerta> listarNaoReconhecidos() {
        return alertaRepository.findAllByReconhecidoFalse();
    }

    public List<Alerta> listarPorEquipamento(Long equipamentoId) {
        return alertaRepository.findAllByEquipamentoId(equipamentoId);
    }

    public List<Alerta> listarNaoReconhecidosPorEquipamento(Long equipamentoId) {
        return alertaRepository.findNaoReconhecidosByEquipamento(equipamentoId);
    }

    public Alerta gerarAlerta(TipoAlerta tipo, Long guinchoId, String descricao) {
        return gerar(guinchoId, null, tipo, descricao);
    }

    public br.com.everrise.dto.response.AlertaResponse reconhecer(Long alertaId) {
        return toResponse(reconhecer(alertaId, null));
    }

    public List<br.com.everrise.dto.response.AlertaResponse> findPendentes(Long guinchoId) {
        return listarNaoReconhecidosPorEquipamento(guinchoId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<br.com.everrise.dto.response.AlertaResponse> findHistorico(Long guinchoId, int limit) {
        return listarPorEquipamento(guinchoId).stream()
                .sorted(Comparator.comparing(Alerta::getGeradoEm).reversed())
                .limit(Math.max(1, limit))
                .map(this::toResponse)
                .toList();
    }

    public br.com.everrise.dto.response.AlertaResponse findById(Long alertaId) {
        return alertaRepository.findById(alertaId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado"));
    }

    private br.com.everrise.dto.response.AlertaResponse toResponse(Alerta alerta) {
        return br.com.everrise.dto.response.AlertaResponse.builder()
                .id(alerta.getId())
                .guinchoId(alerta.getEquipamento() == null ? null : alerta.getEquipamento().getId())
                .tipo(alerta.getTipo())
                .descricao(alerta.getDescricao())
                .reconhecido(alerta.getReconhecido())
                .criadoEm(alerta.getGeradoEm())
                .reconhecidoEm(alerta.getReconhecidoEm())
                .build();
    }
}
