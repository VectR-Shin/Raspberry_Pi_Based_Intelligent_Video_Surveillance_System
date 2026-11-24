package io.github.vectr.team5.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeviceRegistrationRequestDTO(

        @NotBlank
        @Size(max = 50)
        String deviceIdentifier,

        @Size(max = 20)
        String alias

) {
}