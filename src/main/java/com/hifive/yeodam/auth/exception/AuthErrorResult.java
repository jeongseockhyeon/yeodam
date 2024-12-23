package com.hifive.yeodam.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorResult {

    DUPLICATED_EMAIL_JOIN(HttpStatus.BAD_REQUEST, "Duplicated Email Join Request"),
    AUTH_NOT_FOUND(HttpStatus.NOT_FOUND, "Auth Not Found"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
