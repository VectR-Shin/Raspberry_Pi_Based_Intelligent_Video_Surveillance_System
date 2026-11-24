package io.github.vectr.team5.service;

import io.github.vectr.team5.dto.DeviceResponseDTO;
import io.github.vectr.team5.dto.PageResponseDTO;
import io.github.vectr.team5.entity.Device;
import io.github.vectr.team5.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceQueryService {

    private static final int PAGE_SIZE = 10;

    private final DeviceRepository deviceRepository;

    public PageResponseDTO<DeviceResponseDTO> getDevices(
            String userId,
            String alias,
            int page
    ) {
        if (page < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }

        Pageable pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "registeredAt")
        );

        Page<Device> devicePage;

        if (alias == null || alias.isBlank()) {
            devicePage = deviceRepository.findByUserId(userId, pageable);
        } else {
            devicePage = deviceRepository.findByUserIdAndAliasContaining(
                    userId,
                    alias.trim(),
                    pageable
            );
        }

        return new PageResponseDTO<>(
                devicePage.getContent()
                        .stream()
                        .map(DeviceResponseDTO::from)
                        .toList(),
                devicePage.getNumber(),
                devicePage.getSize(),
                devicePage.getTotalElements(),
                devicePage.getTotalPages()
        );
    }
}