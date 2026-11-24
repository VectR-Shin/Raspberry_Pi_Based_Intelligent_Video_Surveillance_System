package io.github.vectr.team5.dto;

import io.github.vectr.team5.enums.EventType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record LogRequestDTO(

        @NotNull(message = "감지 유형은 필수입니다.")
        EventType eventType,

        @NotNull(message = "감지 일시는 필수입니다.")
        LocalDateTime detectedAt

) {
}