package br.com.everrise.service;

import br.com.everrise.dto.request.ChatMessageRequest;
import br.com.everrise.dto.response.ChatMessageResponse;

import java.util.List;

public interface ChatbotService {

    List<String> streamReplyChunks(ChatMessageRequest request);

    void persistConversation(ChatMessageRequest request, String fullBotResponse);

    List<ChatMessageResponse> history(String sessionId);
}
