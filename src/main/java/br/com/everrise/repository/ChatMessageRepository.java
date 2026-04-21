package br.com.everrise.repository;

import br.com.everrise.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, JpaSpecificationExecutor<ChatMessage> {

    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    List<ChatMessage> findBySessionIdAndUserIdOrderByCreatedAtAsc(String sessionId, Long userId);

    List<ChatMessage> findBySessionIdAndUserIsNullOrderByCreatedAtAsc(String sessionId);
}
