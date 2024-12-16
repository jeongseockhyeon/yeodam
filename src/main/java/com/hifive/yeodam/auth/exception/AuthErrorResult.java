package com.hifive.yeodam.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorResult {

    DUPLICATED_AUTH_JOIN(HttpStatus.BAD_REQUEST, "Duplicated Auth Join Request"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
