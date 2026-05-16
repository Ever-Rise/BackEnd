package br.com.everrise.service;

import br.com.everrise.domain.ChatMensagem;
import br.com.everrise.domain.ChatSessao;
import br.com.everrise.domain.Paciente;
import br.com.everrise.domain.enums.RemetenteMensagem;
import br.com.everrise.repository.ChatMensagemRepository;
import br.com.everrise.repository.ChatSessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessaoRepository chatSessaoRepository;
    private final ChatMensagemRepository chatMensagemRepository;
    private final PacienteService pacienteService;

    @Transactional
    public ChatSessao abrirSessao(Long pacienteId, Long usuarioId) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId);
        ChatSessao chatSessao = ChatSessao.builder()
                .paciente(paciente)
                .usuario(usuarioId == null ? null : Paciente.builder().id(usuarioId).build())
                .criadaEm(LocalDateTime.now())
                .build();
        return chatSessaoRepository.save(chatSessao);
    }

    public ChatSessao buscarSessaoPorId(Long id) {
        return chatSessaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão de chat não encontrada"));
    }

    public List<ChatSessao> listarSessoesPorPaciente(Long pacienteId) {
        return chatSessaoRepository.findAllByPacienteId(pacienteId);
    }

    @Transactional
    public ChatMensagem enviarMensagem(Long sessaoId, RemetenteMensagem remetente, String conteudo) {
        ChatSessao chatSessao = buscarSessaoPorId(sessaoId);
        chatSessao.setAtualizadaEm(LocalDateTime.now());
        chatSessaoRepository.save(chatSessao);

        ChatMensagem mensagem = ChatMensagem.builder()
                .chatSessao(chatSessao)
                .remetente(remetente)
                .conteudo(conteudo)
                .enviadaEm(LocalDateTime.now())
                .build();
        return chatMensagemRepository.save(mensagem);
    }

    public List<ChatMensagem> listarMensagensDaSessao(Long sessaoId) {
        return chatMensagemRepository.findMensagensBySessaoOrdenadas(sessaoId);
    }
}

