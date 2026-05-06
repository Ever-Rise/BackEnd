package br.com.everrise.mapper;

import br.com.everrise.domain.entity.Guincho;
import br.com.everrise.dto.response.GuinchoResponse;
import br.com.everrise.dto.response.GuinchoStatusResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GuinchoMapper {

    GuinchoResponse toResponse(Guincho guincho);

    GuinchoStatusResponse toStatusResponse(Guincho guincho);
}
