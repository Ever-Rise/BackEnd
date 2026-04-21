package br.com.everrise.mapper;

import br.com.everrise.domain.entity.Plano;
import br.com.everrise.dto.response.PlanoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanoMapper {

    PlanoResponse toResponse(Plano plano);
}

