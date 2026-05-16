package br.com.everrise.service;

import br.com.everrise.domain.Paciente;
import br.com.everrise.domain.SessaoUso;
import br.com.everrise.domain.enums.StatusEquipamento;
import br.com.everrise.domain.enums.StatusSessao;
import br.com.everrise.repository.SessaoUsoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessaoUsoService {

    private final SessaoUsoRepository sessaoUsoRepository;
    private final EquipamentoService equipamentoService;

    @Transactional
    public SessaoUso iniciar(Long equipamentoId, Long operadorId, Long pacienteId) {
        var equipamento = equipamentoService.buscarPorId(equipamentoId);
        if (equipamento.getStatus() != StatusEquipamento.PRONTO) {
            throw new RuntimeException("Equipamento não está disponível para iniciar sessão");
        }
        if (sessaoUsoRepository.findSessaoAtivaByEquipamento(equipamentoId).isPresent()) {
            throw new RuntimeException("Já existe uma sessão ativa para este equipamento");
        }

        SessaoUso sessao = SessaoUso.builder()
                .equipamento(equipamento)
                .operador(Paciente.builder().id(operadorId).build())
                .paciente(pacienteId == null ? null : Paciente.builder().id(pacienteId).build())
                .status(StatusSessao.ATIVA)
                .iniciadaEm(LocalDateTime.now())
                .build();

        equipamentoService.atualizarStatus(equipamentoId, StatusEquipamento.EM_SESSAO);
        return sessaoUsoRepository.save(sessao);
    }

    @Transactional
    public SessaoUso encerrar(Long sessaoId) {
        return finalizarSessao(sessaoId, StatusSessao.ENCERRADA, StatusEquipamento.PRONTO);
    }

    @Transactional
    public SessaoUso interromper(Long sessaoId) {
        return finalizarSessao(sessaoId, StatusSessao.INTERRUPTED, StatusEquipamento.SAFETY_HOLD);
    }

    public SessaoUso buscarPorId(Long id) {
        return sessaoUsoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));
    }

    public List<SessaoUso> listarPorEquipamento(Long equipamentoId) {
        return sessaoUsoRepository.findAllByEquipamentoId(equipamentoId);
    }

    public List<SessaoUso> listarPorOperador(Long operadorId) {
        return sessaoUsoRepository.findAllByOperadorId(operadorId);
    }

    public List<SessaoUso> listarPorPaciente(Long pacienteId) {
        return sessaoUsoRepository.findAllByPacienteId(pacienteId);
    }

    public Optional<SessaoUso> buscarSessaoAtivaDoEquipamento(Long equipamentoId) {
        return sessaoUsoRepository.findSessaoAtivaByEquipamento(equipamentoId);
    }

    private SessaoUso finalizarSessao(Long sessaoId, StatusSessao novoStatus, StatusEquipamento statusEquipamento) {
        SessaoUso sessao = buscarPorId(sessaoId);
        if (sessao.getStatus() != StatusSessao.ATIVA) {
            throw new RuntimeException("Sessão já foi encerrada");
        }

        sessao.setStatus(novoStatus);
        sessao.setEncerradaEm(LocalDateTime.now());
        equipamentoService.atualizarStatus(sessao.getEquipamento().getId(), statusEquipamento);
        return sessaoUsoRepository.save(sessao);
    }
}

