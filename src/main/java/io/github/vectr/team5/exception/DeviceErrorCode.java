package io.github.vectr.team5.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeviceErrorCode implements ErrorCodeType {
    DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 기기입니다."),
    DEVICE_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 등록된 기기입니다."),
    DUPLICATE_DEVICE_ALIAS(HttpStatus.CONFLICT, "이미 사용 중인 별칭입니다.");

    private final HttpStatus status;
    private final String message;
}
