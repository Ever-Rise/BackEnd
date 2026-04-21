package br.com.everrise.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGuinchoRequest {

    @NotBlank
    private String serialNumber;

    private String apelido;
}

