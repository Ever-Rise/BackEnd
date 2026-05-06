package br.com.everrise.mapper;

import br.com.everrise.domain.entity.User;
import br.com.everrise.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}

