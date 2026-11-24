package io.github.vectr.team5.service;

import io.github.vectr.team5.entity.Device;
import io.github.vectr.team5.exception.BusinessException;
import io.github.vectr.team5.exception.DeviceErrorCode;
import io.github.vectr.team5.exception.ErrorCode;
import io.github.vectr.team5.repository.DeviceRepository;
import io.github.vectr.team5.repository.ImageRepository;
import io.github.vectr.team5.repository.LogRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceUnregisterTransactionService {

    private final DeviceRepository deviceRepository;
    private final ImageRepository imageRepository;
    private final LogRepository logRepository;

    public void unregister(
            String keycloakSub,
            Long deviceId
    ) {

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() ->
                        new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND)
                );

        // 소유자 확인
        if (!keycloakSub.equals(device.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // Image 메타데이터 삭제
        imageRepository.deleteAllByDeviceId(deviceId);

        // Log 삭제
        logRepository.deleteAllByDeviceId(deviceId);

        // Device 사용자 연결 정보 초기화
        device.unregister();
    }
}