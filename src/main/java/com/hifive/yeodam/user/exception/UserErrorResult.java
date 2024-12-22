package com.hifive.yeodam.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorResult {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    DUPLICATED_NICKNAME_JOIN(HttpStatus.BAD_REQUEST, "Duplicated Nickname Join Request"),
    UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Exception"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
