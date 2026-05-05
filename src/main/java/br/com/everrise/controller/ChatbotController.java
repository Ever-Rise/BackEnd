package br.com.everrise.controller;

import br.com.everrise.dto.request.ChatMessageRequest;
import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.dto.response.ChatMessageResponse;
import br.com.everrise.dto.response.SseChunkResponse;
import br.com.everrise.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
@Tag(name = "Chatbot", description = "Streaming SSE e historico de conversas")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping(path = "/message", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Enviar mensagem e obter resposta em streaming", description = "Envia uma mensagem ao chatbot e recebe a resposta em tempo real via Server-Sent Events (SSE)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Streaming iniciado com sucesso",
                    content = @Content(mediaType = "text/event-stream", schema = @Schema(implementation = SseChunkResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados invalidos no corpo da requisicao"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Usuario nao autenticado")
    })
    public SseEmitter message(@Valid @RequestBody ChatMessageRequest request) {
        SseEmitter emitter = new SseEmitter(0L);

        new Thread(() -> {
            try {
                List<String> chunks = chatbotService.streamReplyChunks(request);
                StringBuilder full = new StringBuilder();
                for (String chunk : chunks) {
                    full.append(chunk);
                    emitter.send(SseEmitter.event().name("message").data(SseChunkResponse.builder().chunk(chunk).done(false).build()));
                }
                emitter.send(SseEmitter.event().name("message").data(SseChunkResponse.builder().chunk("").done(true).build()));
                chatbotService.persistConversation(request, full.toString());
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }).start();

        return emitter;
    }

    @GetMapping("/history/{sessionId}")
    @Operation(summary = "Obter historico de conversas", description = "Retorna todo o historico de conversas de uma sessao")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historico carregado com sucesso",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ChatMessageResponse.class)))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Sessao nao encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Usuario nao autenticado")
    })
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> history(
            @Parameter(description = "ID da sessao de chat", example = "session-123", required = true)
            @PathVariable String sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(chatbotService.history(sessionId), "Historico carregado"));
    }
}

