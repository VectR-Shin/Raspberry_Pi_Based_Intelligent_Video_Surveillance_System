package io.github.vectr.team5.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeviceAliasUpdateRequestDTO(

        @NotBlank(message = "별칭은 필수 입력값입니다.")
        @Size(max = 20, message = "별칭은 최대 20자까지 입력할 수 있습니다.")
        String alias

) {
}