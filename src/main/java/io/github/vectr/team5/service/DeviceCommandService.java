package io.github.vectr.team5.service;

import io.github.vectr.team5.dto.DeviceAliasUpdateRequestDTO;
import io.github.vectr.team5.dto.DeviceRegistrationRequestDTO;
import io.github.vectr.team5.dto.DeviceResponseDTO;
import io.github.vectr.team5.entity.Device;
import io.github.vectr.team5.exception.BusinessException;
import io.github.vectr.team5.exception.DeviceErrorCode;
import io.github.vectr.team5.exception.ErrorCode;
import io.github.vectr.team5.repository.DeviceRepository;
import io.github.vectr.team5.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceCommandService {

    private final DeviceRepository deviceRepository;
    private final ImageRepository imageRepository;

    private final ImageStorageService imageStorageService;
    private final DeviceUnregisterTransactionService transactionService;

    public void unregisterDevice(
            String keycloakSub,
            Long deviceId
    ) {

        // 1. 기기 조회
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() ->
                        new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND)
                );

        // 2. 기기 소유자 확인
        if (!keycloakSub.equals(device.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 3. 이미지 파일 경로 조회
        List<String> storagePaths = imageRepository.findStoragePathsByDeviceId(deviceId);

        // 4. 실제 이미지 파일 삭제
        imageStorageService.deleteImages(storagePaths);

        // 5. DB 트랜잭션 실행
        transactionService.unregister(keycloakSub, deviceId);
    }

    @Transactional
    public DeviceResponseDTO registerDevice(
            String keycloakSub,
            DeviceRegistrationRequestDTO request
    ) {
        // 1. 기존 기기 조회
        Device device = deviceRepository
                .findByDeviceIdentifier(request.deviceIdentifier())
                .orElseThrow(() ->
                        new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND)
                );

        // 2. 이미 사용자에게 등록된 기기인지 확인
        if (device.getUserId() != null) {
            throw new BusinessException(DeviceErrorCode.DEVICE_ALREADY_REGISTERED);
        }

        // 3. 사용자별 별칭 중복 확인
        if (deviceRepository.existsByUserIdAndAlias(
                keycloakSub,
                request.alias()
        )) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // 4. 기존 기기에 사용자 정보 연결
        device.register(keycloakSub, request.alias());

        // 5. 응답 DTO 반환
        return DeviceResponseDTO.from(device);
    }

    @Transactional
    public DeviceResponseDTO updateAlias(
            String keycloakSub,
            Long deviceId,
            DeviceAliasUpdateRequestDTO request
    ) {
        // 1. 기기 조회
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() ->
                        new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND)
                );

        // 2. 소유자 확인
        if (!keycloakSub.equals(device.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 3. 별칭 정규화
        String alias = request.alias().trim();

        // 4. 별칭 중복 확인 (현재 기기 제외)
        if (deviceRepository.existsByUserIdAndAliasAndIdNot(
                keycloakSub,
                alias,
                deviceId
        )) {
            throw new BusinessException(DeviceErrorCode.DUPLICATE_DEVICE_ALIAS);
        }

        // 5. 별칭 변경
        device.updateAlias(alias);

        return DeviceResponseDTO.from(device);
    }
}
