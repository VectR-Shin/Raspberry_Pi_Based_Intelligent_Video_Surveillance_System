package io.github.vectr.team5.service;

import java.time.LocalDateTime;

import io.github.vectr.team5.dto.LogDataRequestDTO;
import io.github.vectr.team5.dto.LogResponseDTO;
import io.github.vectr.team5.dto.PageResponseDTO;
import io.github.vectr.team5.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogQueryService {

    private static final int PAGE_SIZE = 20;

    private final LogRepository logRepository;

    public PageResponseDTO<LogResponseDTO> getLogs(
            String userId,
            LogDataRequestDTO request
    ) {
        validateRequest(request);

        LocalDateTime startDateTime = request.startDate() == null
                ? null
                : request.startDate().atStartOfDay();

        LocalDateTime endDateTime = request.endDate() == null
                ? null
                : request.endDate().plusDays(1).atStartOfDay();

        Pageable pageable = PageRequest.of(
                request.page(),
                PAGE_SIZE
        );

        Page<LogResponseDTO> result = logRepository.findLogs(
                userId,
                startDateTime,
                endDateTime,
                request.alias(),
                request.eventType(),
                pageable
        );

        return new PageResponseDTO<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private void validateRequest(LogDataRequestDTO request) {

        if ((request.startDate() == null) != (request.endDate() == null)) {
            throw new IllegalArgumentException(
                    "시작일과 종료일을 모두 지정해야 합니다."
            );
        }

        if (request.startDate() != null
                && request.startDate().isAfter(request.endDate())) {
            throw new IllegalArgumentException(
                    "시작일은 종료일보다 늦을 수 없습니다."
            );
        }

        if (request.page() == null || request.page() < 0) {
            throw new IllegalArgumentException(
                    "페이지 번호는 0 이상이어야 합니다."
            );
        }
    }
}