package br.com.everrise.repository;

import br.com.everrise.domain.ChatMensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMensagemRepository extends JpaRepository<ChatMensagem, Long> {

    List<ChatMensagem> findAllByChatSessaoId(Long chatSessaoId);

    @Query("SELECT m FROM ChatMensagem m WHERE m.chatSessao.id = :sessaoId ORDER BY m.enviadaEm ASC")
    List<ChatMensagem> findMensagensBySessaoOrdenadas(@Param("sessaoId") Long sessaoId);

    long countByChatSessaoId(Long chatSessaoId);
}

