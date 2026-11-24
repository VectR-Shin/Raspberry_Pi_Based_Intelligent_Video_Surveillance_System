package io.github.vectr.team5.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCodeType {

    HttpStatus getStatus();

    String getMessage();
}