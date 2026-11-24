package io.github.vectr.team5.dto;

import io.github.vectr.team5.enums.EventType;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public record LogDataRequestDTO(
        LocalDate startDate,
        LocalDate endDate,
        String alias,
        EventType eventType,
        @Min(0) Integer page
) {
    public LogDataRequestDTO {
        if (page == null) {
            page = 0;
        }
    }
}