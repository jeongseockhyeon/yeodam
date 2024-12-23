package com.hifive.yeodam.global.exception;

import com.hifive.yeodam.auth.exception.AuthErrorResult;
import com.hifive.yeodam.auth.exception.AuthException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<CustomErrorResponseDto> handleCustomException(CustomException e) {
        return CustomErrorResponseDto.toResponseEntity(e.getCustomErrorCode());
    }

    //AuthException용 핸들러
    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<CustomErrorResponseDto> handleAuthException(AuthException e) {
        AuthErrorResult errorResult = e.getErrorResult();
        return ResponseEntity
                .status(errorResult.getHttpStatus())
                .body(CustomErrorResponseDto.builder()
                        .statusCode(errorResult.getHttpStatus().value())
                        .code(errorResult.name())
                        .message(errorResult.getMessage())
                        .build()
                );

    }
}