package br.com.everrise.service;

import br.com.everrise.domain.entity.Alerta;
import br.com.everrise.domain.entity.Guincho;
import br.com.everrise.domain.enums.TipoAlerta;
import br.com.everrise.dto.response.AlertaResponse;
import br.com.everrise.mapper.AlertaMapper;
import br.com.everrise.repository.AlertaRepository;
import br.com.everrise.repository.GuinchoRepository;
import br.com.everrise.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaServiceImpl implements AlertaService {

    private final AlertaRepository alertaRepository;
    private final GuinchoRepository guinchoRepository;
    private final AlertaMapper alertaMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public Alerta gerarAlerta(TipoAlerta tipo, Long guinchoId, String descricao) {
        Guincho guincho = guinchoRepository.findById(guinchoId)
            .orElseThrow(() -> new RuntimeException("Guincho não encontrado"));

        Alerta alerta = Alerta.builder()
            .tipo(tipo)
            .guincho(guincho)
            .descricao(descricao)
            .criadoEm(LocalDateTime.now())
            .reconhecido(false)
            .build();

        return alertaRepository.save(alerta);
    }

    @Override
    @Transactional
    public AlertaResponse reconhecer(Long alertaId) {
        Alerta alerta = alertaRepository.findById(alertaId)
            .orElseThrow(() -> new RuntimeException("Alerta não encontrado"));

        alerta.setReconhecido(true);
        alerta.setReconhecidoEm(LocalDateTime.now());
        alerta.setReconhecidoPor(securityUtils.getCurrentUser());

        return alertaMapper.toResponse(alertaRepository.save(alerta));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaResponse> findPendentes(Long guinchoId) {
        return alertaRepository.findPendentessByGuinchoId(guinchoId)
            .stream()
            .map(alertaMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaResponse> findHistorico(Long guinchoId, int limit) {
        List<Alerta> alertas = alertaRepository.findByGuinchoIdOrderByDataDesc(guinchoId);
        return alertas.stream()
            .limit(limit)
            .map(alertaMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlertaResponse findById(Long alertaId) {
        return alertaRepository.findById(alertaId)
            .map(alertaMapper::toResponse)
            .orElseThrow(() -> new RuntimeException("Alerta não encontrado"));
    }
}
