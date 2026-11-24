package io.github.vectr.team5.service;

import io.github.vectr.team5.dto.LogRequestDTO;
import io.github.vectr.team5.entity.Device;
import io.github.vectr.team5.entity.Image;
import io.github.vectr.team5.entity.Log;
import io.github.vectr.team5.exception.BusinessException;
import io.github.vectr.team5.exception.DeviceErrorCode;
import io.github.vectr.team5.exception.ErrorCode;
import io.github.vectr.team5.repository.DeviceRepository;
import io.github.vectr.team5.repository.ImageRepository;
import io.github.vectr.team5.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LogCommandService {
    private final DeviceRepository deviceRepository;
    private final LogRepository logRepository;
    private final ImageRepository imageRepository;
    private final ImageStorageService imageStorageService;

    @Transactional
    public void createLog(
            String userId,
            String deviceIdentifier,
            LogRequestDTO request,
            MultipartFile image
    ) {
        // 1. 이미지 파일 검증
        if (image == null || image.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // 2. 기기 조회
        Device device = deviceRepository
                .findByDeviceIdentifierAndUserId(deviceIdentifier, userId)
                .orElseThrow(() ->
                        new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND)
                );

        // 3. 이벤트 로그 생성 및 저장
        Log log = new Log(
                device,
                request.eventType(),
                request.detectedAt()
        );

        logRepository.save(log);

        // 4. 이미지 파일 저장
        String storagePath = imageStorageService.save(image);

        // 5. 이미지 메타데이터 생성 및 저장
        Image imageEntity = new Image(
                log,
                storagePath,
                image.getOriginalFilename(),
                image.getContentType(),
                image.getSize()
        );

        imageRepository.save(imageEntity);
    }
}
