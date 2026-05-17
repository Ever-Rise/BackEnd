package br.com.everrise.dto.response;

public record TokenResponse(String token, String tipo) {
    public static TokenResponse bearer(String token) {
        return new TokenResponse(token, "Bearer");
    }
}

