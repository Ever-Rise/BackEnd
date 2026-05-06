package br.com.everrise.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        LocalDateTime timestamp
) {

    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

