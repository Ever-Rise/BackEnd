package br.com.everrise.mapper;

import br.com.everrise.domain.entity.Pedido;
import br.com.everrise.dto.response.PedidoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "planoId", source = "plano.id")
    PedidoResponse toResponse(Pedido pedido);
}

