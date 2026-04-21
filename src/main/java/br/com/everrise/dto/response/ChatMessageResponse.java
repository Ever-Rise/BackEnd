package br.com.everrise.dto.response;

import br.com.everrise.domain.enums.ChatRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private String sessionId;
    private ChatRole role;
    private String content;
    private LocalDateTime createdAt;
}

