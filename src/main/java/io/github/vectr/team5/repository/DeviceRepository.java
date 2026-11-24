package io.github.vectr.team5.repository;

import io.github.vectr.team5.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    // 사용자 식별자로 기기 조회
    Page<Device> findByUserId(String userId, Pageable pageable);

    // 사용자 식별자와 기기 별칭 부분 일치로 기기 조회
    Page<Device> findByUserIdAndAliasContaining(
            String userId,
            String alias,
            Pageable pageable
    );

    // 사용자 식별자, 기기 별칭 중복 확인
    boolean existsByUserIdAndAlias(String userId, String alias);

    // 기기 식별자로 기기 찾기
    Optional<Device> findByDeviceIdentifier(String deviceIdentifier);

    // 기기 식별자와 계정 식별자로 기기 찾기
    Optional<Device> findByDeviceIdentifierAndUserId(String deviceIdentifier, String userId);

    // 현재 수정 중인 기기를 제외하고 같은 사용자의 다른 기기에 동일한 별칭이 있는지 확인
    boolean existsByUserIdAndAliasAndIdNot(String userId, String alias, Long deviceId);
}
