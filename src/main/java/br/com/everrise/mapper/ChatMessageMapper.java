package br.com.everrise.mapper;

import br.com.everrise.domain.entity.ChatMessage;
import br.com.everrise.dto.response.ChatMessageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    ChatMessageResponse toResponse(ChatMessage message);
}

