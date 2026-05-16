package br.com.everrise.service;

import br.com.everrise.domain.Paciente;
import br.com.everrise.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public List<Paciente> listarAtivos() {
        return pacienteRepository.findAllByAtivoTrue();
    }

    @Transactional
    public Paciente criar(Paciente paciente) {
        if (paciente.getCriadoEm() == null) {
            paciente.setCriadoEm(LocalDateTime.now());
        }
        if (paciente.getAtivo() == null) {
            paciente.setAtivo(true);
        }
        return pacienteRepository.save(paciente);
    }

    @Transactional
    public Paciente atualizar(Long id, Paciente dados) {
        Paciente existente = buscarPorId(id);

        if (dados.getNome() != null) {
            existente.setNome(dados.getNome());
        }
        if (dados.getEmail() != null) {
            existente.setEmail(dados.getEmail());
        }
        if (dados.getSenha() != null) {
            existente.setSenha(dados.getSenha());
        }
        if (dados.getRole() != null) {
            existente.setRole(dados.getRole());
        }
        if (dados.getAtivo() != null) {
            existente.setAtivo(dados.getAtivo());
        }
        if (dados.getCriadoEm() != null) {
            existente.setCriadoEm(dados.getCriadoEm());
        }
        if (dados.getDataNascimento() != null) {
            existente.setDataNascimento(dados.getDataNascimento());
        }
        if (dados.getCondicaoMedica() != null) {
            existente.setCondicaoMedica(dados.getCondicaoMedica());
        }
        if (dados.getObservacoes() != null) {
            existente.setObservacoes(dados.getObservacoes());
        }
        if (dados.getFamiliares() != null) {
            existente.setFamiliares(dados.getFamiliares());
        }

        return pacienteRepository.save(existente);
    }

    @Transactional
    public void desativar(Long id) {
        Paciente paciente = buscarPorId(id);
        paciente.setAtivo(false);
        pacienteRepository.save(paciente);
    }

    public List<Paciente> buscarPacientesPorFamiliar(Long idUsuario) {
        return pacienteRepository.findPacientesByFamiliarId(idUsuario);
    }
}

