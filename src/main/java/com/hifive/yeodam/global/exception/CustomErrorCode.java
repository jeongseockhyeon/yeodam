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

    //주문 관련 ERROR
    NOT_ENOUGH_STOCK(HttpStatus.NOT_FOUND, "해당 상품의 재고가 없습니다"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문이 존재하지 않습니다"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 결제 내역이 존재하지 않습니다."),
    PAYMENT_FAILED(HttpStatus.NOT_FOUND, "결제에 실패했습니다"),
    PAYMENT_CANCELED(HttpStatus.NOT_FOUND, "결제 취소에 실패했습니다"),
    I_AM_PORT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "아이엠 포트 결제 조회에 실패했습니다"),
    ;


    private final HttpStatus httpStatus;
    private final String message;
}
