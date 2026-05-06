package br.com.everrise.mapper;

import br.com.everrise.domain.entity.TelemetryRecord;
import br.com.everrise.dto.response.TelemetryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TelemetryMapper {

    @Mapping(target = "guinchoId", source = "guincho.id")
    TelemetryResponse toResponse(TelemetryRecord telemetryRecord);
}

