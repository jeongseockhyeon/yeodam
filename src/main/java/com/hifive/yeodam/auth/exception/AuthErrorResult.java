package com.hifive.yeodam.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorResult {

    DUPLICATED_EMAIL_JOIN(HttpStatus.BAD_REQUEST, "Duplicated Email Join Request"),
    AUTH_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"), //로그인 인증 정보용
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다"), //사용자 추가 정보용
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
