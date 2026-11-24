package io.github.vectr.team5.service;

import io.github.vectr.team5.exception.BusinessException;
import io.github.vectr.team5.exception.ErrorCode;
import io.github.vectr.team5.exception.ImageErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ImageStorageService {

    private final Path imageDirectory;

    public ImageStorageService(
            @Value("${app.storage.image-dir}") String imageDir
    ) {
        this.imageDirectory = Path.of(imageDir)
                .toAbsolutePath()
                .normalize();
    }

    public String save(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        String extension = getExtension(image.getContentType());

        String filename = UUID.randomUUID() + extension;

        Path destination = imageDirectory.resolve(filename).normalize();

        try {
            Files.createDirectories(imageDirectory);

            image.transferTo(destination);

            // DB에는 이미지 저장 디렉터리 기준 상대 경로 저장
            return filename;

        } catch (IOException e) {
            log.error("이미지 파일 저장 실패", e);
            throw new BusinessException(ImageErrorCode.IMAGE_STORAGE_FAILED);
        }
    }

    private String getExtension(String contentType) {

        if ("image/jpeg".equalsIgnoreCase(contentType)) {
            return ".jpg";
        }

        if ("image/png".equalsIgnoreCase(contentType)) {
            return ".png";
        }

        if ("image/webp".equalsIgnoreCase(contentType)) {
            return ".webp";
        }

        throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }

    public void deleteImages(List<String> storagePaths) {

        for (String storagePath : storagePaths) {

            Path imagePath = imageDirectory
                    .resolve(storagePath)
                    .normalize();

            // 저장 디렉토리 외부의 파일 접근 방지
            if (!imagePath.startsWith(imageDirectory)) {
                throw new IllegalArgumentException(
                        "잘못된 이미지 저장 경로입니다: " + storagePath
                );
            }

            try {
                Files.deleteIfExists(imagePath);

            } catch (IOException e) {
                log.error(
                        "이미지 파일 삭제 실패: {}",
                        imagePath,
                        e
                );

                throw new IllegalStateException(
                        "이미지 파일 삭제에 실패했습니다.",
                        e
                );
            }
        }
    }
}
