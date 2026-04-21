package br.com.everrise.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_CLINICA;

    @Override
    public String getAuthority() {
        return name();
    }

    @JsonValue
    public String value() {
        return name();
    }
}
