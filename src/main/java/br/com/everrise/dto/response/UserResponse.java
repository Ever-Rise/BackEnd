package br.com.everrise.dto.response;

import br.com.everrise.dto.response.PlanoResponse;
import br.com.everrise.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private PlanoResponse plano;
}

