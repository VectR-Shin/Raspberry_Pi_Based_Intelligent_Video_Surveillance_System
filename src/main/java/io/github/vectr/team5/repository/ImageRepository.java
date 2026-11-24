package io.github.vectr.team5.repository;

import io.github.vectr.team5.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // 이미지를 소유한 사용자를 확인
    Optional<Image> findByIdAndLog_Device_UserId(
            Long imageId,
            String userId
    );

    // 특정 장치에 의해 기록된 이미지 경로 리스트 조회
    @Query("""
        SELECT i.storagePath
        FROM Image i
        WHERE i.log.device.id = :deviceId
        """)
    List<String> findStoragePathsByDeviceId(@Param("deviceId") Long deviceId);

    // 특정 장치에 의해 기록된 이미지 메타데이터 삭제
    @Modifying
    @Query("""
        DELETE FROM Image i
        WHERE i.log.device.id = :deviceId
        """)
    void deleteAllByDeviceId(@Param("deviceId") Long deviceId);

}
