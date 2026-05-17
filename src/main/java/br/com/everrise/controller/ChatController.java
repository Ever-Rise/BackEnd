package br.com.everrise.controller;

import br.com.everrise.dto.request.AbrirChatRequest;
import br.com.everrise.dto.request.EnviarMensagemRequest;
import br.com.everrise.dto.response.ChatMensagemResponse;
import br.com.everrise.dto.response.ChatSessaoResponse;
import br.com.everrise.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/sessoes")
    @Operation(summary = "Abrir sessão de chat")
    public ResponseEntity<ChatSessaoResponse> abrirSessao(@Valid @RequestBody AbrirChatRequest request) {
        return ResponseEntity.status(201).body(ChatSessaoResponse.from(chatService.abrirSessao(request.pacienteId(), request.usuarioId())));
    }

    @GetMapping("/sessoes/paciente/{id}")
    @Operation(summary = "Listar sessões por paciente")
    public ResponseEntity<List<ChatSessaoResponse>> listarSessoesPorPaciente(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.listarSessoesPorPaciente(id).stream().map(ChatSessaoResponse::from).toList());
    }

    @PostMapping("/sessoes/{id}/mensagens")
    @Operation(summary = "Enviar mensagem")
    public ResponseEntity<ChatMensagemResponse> enviarMensagem(@PathVariable Long id, @Valid @RequestBody EnviarMensagemRequest request) {
        return ResponseEntity.status(201).body(ChatMensagemResponse.from(
                chatService.enviarMensagem(id, request.remetente(), request.conteudo())
        ));
    }

    @GetMapping("/sessoes/{id}/mensagens")
    @Operation(summary = "Listar mensagens da sessão")
    public ResponseEntity<List<ChatMensagemResponse>> listarMensagens(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.listarMensagensDaSessao(id).stream().map(ChatMensagemResponse::from).toList());
    }
}

