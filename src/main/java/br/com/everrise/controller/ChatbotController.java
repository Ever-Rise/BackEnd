package br.com.everrise.controller;

import br.com.everrise.dto.request.ChatMessageRequest;
import br.com.everrise.dto.response.ApiResponse;
import br.com.everrise.dto.response.ChatMessageResponse;
import br.com.everrise.dto.response.SseChunkResponse;
import br.com.everrise.service.ChatbotService;
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
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> history(@PathVariable String sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(chatbotService.history(sessionId), "Historico carregado"));
    }
}
