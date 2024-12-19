package com.hifive.yeodam.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {


    /**
     * 404 NOT_FOUND
     * 요청 리소스가 없음
     */
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리가 존재하지 않습니다"),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품이 존재하지 않습니다"),
    GUIDE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 가이드가 존재하지 않습니다"),
    ;


    private final HttpStatus httpStatus;
    private final String message;
}
