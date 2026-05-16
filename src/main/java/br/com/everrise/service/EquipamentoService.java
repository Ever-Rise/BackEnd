package br.com.everrise.service;

import br.com.everrise.domain.Equipamento;
import br.com.everrise.domain.enums.StatusEquipamento;
import br.com.everrise.repository.EquipamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;

    public Equipamento buscarPorId(Long id) {
        return equipamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));
    }

    public Equipamento buscarPorIdentificador(String identificador) {
        return equipamentoRepository.findByIdentificador(identificador)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));
    }

    public List<Equipamento> listarTodos() {
        return equipamentoRepository.findAll();
    }

    public List<Equipamento> listarPorStatus(StatusEquipamento status) {
        return equipamentoRepository.findAllByStatus(status);
    }

    public List<Equipamento> listarDisponiveis() {
        return equipamentoRepository.findDisponiveis(List.of(StatusEquipamento.PRONTO, StatusEquipamento.DESLIGADO));
    }

    @Transactional
    public Equipamento criar(Equipamento equipamento) {
        if (equipamentoRepository.existsByIdentificador(equipamento.getIdentificador())) {
            throw new RuntimeException("Identificador do equipamento já cadastrado");
        }
        if (equipamento.getUltimaAtualizacao() == null) {
            equipamento.setUltimaAtualizacao(LocalDateTime.now());
        }
        return equipamentoRepository.save(equipamento);
    }

    @Transactional
    public Equipamento atualizarStatus(Long id, StatusEquipamento novoStatus) {
        Equipamento equipamento = buscarPorId(id);
        equipamento.setStatus(novoStatus);
        equipamento.setUltimaAtualizacao(LocalDateTime.now());
        return equipamentoRepository.save(equipamento);
    }

    @Transactional
    public void atualizarTelemetria(Long id, Integer bateria, String localizacao) {
        Equipamento equipamento = buscarPorId(id);
        equipamento.setBateria(bateria);
        equipamento.setLocalizacao(localizacao);
        equipamento.setUltimaAtualizacao(LocalDateTime.now());
        equipamentoRepository.save(equipamento);
    }
}

