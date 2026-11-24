package io.github.vectr.team5.repository;


import java.time.LocalDateTime;

import io.github.vectr.team5.dto.LogResponseDTO;
import io.github.vectr.team5.entity.Log;
import io.github.vectr.team5.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LogRepository extends JpaRepository<Log, Long> {
    @Query(
            value = """
            SELECT new io.github.vectr.team5.dto.LogResponseDTO(
                l.id,
                d.deviceIdentifier,
                d.alias,
                l.eventType,
                l.detectedAt,
                i.id
            )
            FROM Log l
            JOIN l.device d
            JOIN Image i ON i.log = l
            WHERE d.userId = :userId
              AND (:startDateTime IS NULL OR l.detectedAt >= :startDateTime)
              AND (:endDateTime IS NULL OR l.detectedAt < :endDateTime)
              AND (:alias IS NULL OR d.alias LIKE :alias)
              AND (:eventType IS NULL OR l.eventType = :eventType)
            """,
            countQuery = """
            SELECT COUNT(l)
            FROM Log l
            JOIN l.device d
            JOIN Image i ON i.log = l
            WHERE d.userId = :userId
              AND (:startDateTime IS NULL OR l.detectedAt >= :startDateTime)
              AND (:endDateTime IS NULL OR l.detectedAt < :endDateTime)
              AND (:alias IS NULL OR d.alias LIKE :alias)
              AND (:eventType IS NULL OR l.eventType = :eventType)
            """
    )
    Page<LogResponseDTO> findLogs(
            @Param("userId") String userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("alias") String alias,
            @Param("eventType") EventType eventType,
            Pageable pageable
    );

    // 특정 기기에 의해 등록된 모든 로그 삭제
    @Modifying
    @Query("""
        DELETE FROM Log l
        WHERE l.device.id = :deviceId
        """)
    void deleteAllByDeviceId(@Param("deviceId") Long deviceId);
}
