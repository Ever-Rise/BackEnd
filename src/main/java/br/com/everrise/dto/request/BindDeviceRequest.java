package br.com.everrise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindDeviceRequest {

    @NotNull
    private Long guinchoId;

    @NotBlank
    private String deviceFingerprint;
}
