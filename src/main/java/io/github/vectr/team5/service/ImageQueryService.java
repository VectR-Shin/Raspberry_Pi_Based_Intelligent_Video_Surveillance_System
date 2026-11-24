package io.github.vectr.team5.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.vectr.team5.entity.Image;
import io.github.vectr.team5.exception.BusinessException;
import io.github.vectr.team5.exception.ErrorCode;
import io.github.vectr.team5.exception.ImageErrorCode;
import io.github.vectr.team5.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.vectr.team5.dto.ImageFileResponse;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageQueryService {

    private final ImageRepository imageRepository;

    @Value("${app.storage.image-dir}")
    private String imageDir;

    public ImageFileResponse getImage(Long imageId, String keycloakSub) {

        // 1. 이미지 조회 및 소유권 확인
        Image image = imageRepository
                .findByIdAndLog_Device_UserId(imageId, keycloakSub)
                .orElseThrow(() ->
                        new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND)
                );

        // 2. 파일 경로 확인
        Path imageDirectory = Path.of(imageDir)
                .toAbsolutePath()
                .normalize();

        Path path = imageDirectory
                .resolve(image.getStoragePath())
                .normalize();

        System.out.println("이미지 저장 디렉터리: " + imageDirectory);
        System.out.println("DB 저장 경로: " + image.getStoragePath());
        System.out.println("최종 이미지 경로: " + path);
        System.out.println("디렉터리 포함 여부: " + path.startsWith(imageDirectory));
        System.out.println("파일 존재 여부: " + Files.exists(path));

        // 저장 디렉터리 외부의 파일 접근 방지
        if (!path.startsWith(imageDirectory) || path.equals(imageDirectory)) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 3. 파일 읽기
        byte[] data;

        try {
            if (!Files.isRegularFile(path)) {
                throw new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND);
            }

            data = Files.readAllBytes(path);

        } catch (IOException e) {
            log.error("이미지 파일 읽기 실패. path={}", path, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 4. Content-Type 생성
        MediaType contentType;

        try {
            contentType = MediaType.parseMediaType(image.getContentType());
        } catch (IllegalArgumentException e) {
            log.error("이미지 Content-Type 파싱 실패. contentType={}", image.getContentType(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return new ImageFileResponse(data, contentType);
    }
}