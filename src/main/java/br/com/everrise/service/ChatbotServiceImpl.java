package br.com.everrise.service;

import br.com.everrise.domain.entity.ChatMessage;
import br.com.everrise.domain.entity.User;
import br.com.everrise.domain.enums.ChatRole;
import br.com.everrise.dto.request.ChatMessageRequest;
import br.com.everrise.dto.response.ChatMessageResponse;
import br.com.everrise.mapper.ChatMessageMapper;
import br.com.everrise.repository.ChatMessageRepository;
import br.com.everrise.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final SecurityUtils securityUtils;

    @Override
    public List<String> streamReplyChunks(ChatMessageRequest request) {
        // TODO: Integrar chamada real ao Gemini Flash com streaming quando o gateway HTTP for adicionado.
        String simulated = "Resposta assistente EVERRISE: " + request.getContent();
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < simulated.length(); i += 24) {
            chunks.add(simulated.substring(i, Math.min(simulated.length(), i + 24)));
        }
        return chunks;
    }

    @Override
    @Transactional
    public void persistConversation(ChatMessageRequest request, String fullBotResponse) {
        User currentUser;
        try {
            currentUser = securityUtils.getCurrentUser();
        } catch (Exception ex) {
            currentUser = null;
        }

        chatMessageRepository.save(ChatMessage.builder()
                .sessionId(request.getSessionId())
                .user(currentUser)
                .role(ChatRole.USER)
                .content(request.getContent())
                .build());

        chatMessageRepository.save(ChatMessage.builder()
                .sessionId(request.getSessionId())
                .user(currentUser)
                .role(ChatRole.BOT)
                .content(fullBotResponse)
                .build());
    }

    @Override
    public List<ChatMessageResponse> history(String sessionId) {
        User currentUser;
        try {
            currentUser = securityUtils.getCurrentUser();
        } catch (Exception ex) {
            currentUser = null;
        }

        List<ChatMessage> messages = currentUser != null
                ? chatMessageRepository.findBySessionIdAndUserIdOrderByCreatedAtAsc(sessionId, currentUser.getId())
                : chatMessageRepository.findBySessionIdAndUserIsNullOrderByCreatedAtAsc(sessionId);

        return messages.stream().map(chatMessageMapper::toResponse).toList();
    }
}
