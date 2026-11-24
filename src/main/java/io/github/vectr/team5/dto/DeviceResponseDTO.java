package io.github.vectr.team5.dto;

import io.github.vectr.team5.entity.Device;

import java.time.LocalDateTime;

public record DeviceResponseDTO(
        Long id,
        String deviceIdentifier,
        String alias,
        LocalDateTime registeredAt
) {
    public static DeviceResponseDTO from(Device device) {
        return new DeviceResponseDTO(
                device.getId(),
                device.getDeviceIdentifier(),
                device.getAlias(),
                device.getRegisteredAt()
        );
    }
}