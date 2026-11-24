package io.github.vectr.team5.dto;

import io.github.vectr.team5.enums.EventType;

import java.time.LocalDateTime;

public record LogResponseDTO(
        Long id,
        String deviceIdentifier,
        String alias,
        EventType eventType,
        LocalDateTime detectedAt,
        Long imageId
) {
}